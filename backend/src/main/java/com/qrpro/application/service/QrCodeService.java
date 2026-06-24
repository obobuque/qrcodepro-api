package com.qrpro.application.service;

import com.qrpro.application.dto.response.QrCodeImageResponse;
import com.qrpro.domain.model.ScanEvent;
import com.qrpro.domain.repository.ScanEventRepositoryPort;
import java.util.List;

import com.qrpro.application.port.in.GenerateQrCodeUseCase;
import com.qrpro.application.port.in.UpdateDynamicDestinationUseCase;
import com.qrpro.application.port.out.QrCodeCachePort;
import com.qrpro.application.port.out.QrCodeStoragePort;
import com.qrpro.domain.model.QrCode;
import com.qrpro.domain.repository.QrCodeRepositoryPort;
import com.qrpro.domain.service.QrCodeGenerator;
import com.qrpro.domain.service.QrCodeValidator;
import com.qrpro.domain.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class QrCodeService implements GenerateQrCodeUseCase, UpdateDynamicDestinationUseCase {

    private final QrCodeRepositoryPort qrCodeRepository;
    private final QrCodeGenerator qrCodeGenerator;
    private final QrCodeStoragePort storagePort;
    private final QrCodeCachePort cachePort;
    private final QrCodeValidator validator;
    private final ScanEventRepositoryPort scanEventRepository;
    private final PlanEnforcementService planEnforcement;

    @Override
    public QrCode generateStatic(StaticQrCommand command) {
        var ownerId = UUID.fromString(command.ownerId());
        planEnforcement.enforceQrCreation(ownerId);
        var content = new QrCodeContent(command.content());
        var design = command.design() != null ? command.design() : QrCodeDesign.defaultDesign();
        validator.validateContent(content.value());
        var qrCode = QrCode.createStatic(ownerId, content, design);
        byte[] imageData = qrCodeGenerator.generate(qrCode);
        String fileName = "qr-static-" + qrCode.getId() + ".png";
        String imageUrl = storagePort.store(imageData, fileName);
        qrCode.setImageUrl(imageUrl);
        return qrCodeRepository.save(qrCode);
    }

    @Override
    public QrCode generateDynamic(DynamicQrCommand command) {
        var ownerId = UUID.fromString(command.ownerId());
        planEnforcement.enforceQrCreation(ownerId);
        planEnforcement.enforceDynamicQr(ownerId);
        var destination = new DynamicDestination(command.initialUrl());
        var design = command.design() != null ? command.design() : QrCodeDesign.defaultDesign();
        var shortCode = generateUniqueShortCode();
        var qrCode = QrCode.createDynamic(ownerId, shortCode, destination, design);
        byte[] imageData = qrCodeGenerator.generate(qrCode);
        String fileName = "qr-dynamic-" + shortCode.value() + ".png";
        String imageUrl = storagePort.store(imageData, fileName);
        qrCode.setImageUrl(imageUrl);
        cachePort.cacheDestination(shortCode, destination.url());
        return qrCodeRepository.save(qrCode);
    }

    @Override
    public QrCode updateDestination(String qrCodeId, UpdateDestinationCommand command) {
        var qrCode = qrCodeRepository.findById(UUID.fromString(qrCodeId))
            .orElseThrow(() -> new IllegalArgumentException("QR code not found: " + qrCodeId));
        var newDestination = new DynamicDestination(command.newUrl());
        if (qrCode.getShortCode() != null) {
            cachePort.invalidate(qrCode.getShortCode());
        }
        qrCode.updateDestination(newDestination);
        if (qrCode.getShortCode() != null) {
            cachePort.cacheDestination(qrCode.getShortCode(), newDestination.url());
        }
        return qrCodeRepository.save(qrCode);
    }

    @Transactional(readOnly = true)
    public List<QrCode> listByOwner(java.util.UUID ownerId) {
        return qrCodeRepository.findByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    public QrCode getById(java.util.UUID id) {
        return qrCodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("QR code not found: " + id));
    }

    @Transactional
    public void delete(java.util.UUID id) {
        qrCodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("QR code not found: " + id));
        qrCodeRepository.deleteById(id);
    }

    @Transactional
    public QrCode deactivate(java.util.UUID id) {
        var qrCode = qrCodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("QR code not found: " + id));
        qrCode.deactivate();
        return qrCodeRepository.save(qrCode);
    }

    @Transactional
    public QrCode activate(java.util.UUID id) {
        var qrCode = qrCodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("QR code not found: " + id));
        qrCode.activate();
        return qrCodeRepository.save(qrCode);
    }

    @Transactional(readOnly = true)
    public List<ScanEvent> getScanHistory(java.util.UUID qrCodeId) {
        qrCodeRepository.findById(qrCodeId)
                .orElseThrow(() -> new IllegalArgumentException("QR code not found: " + qrCodeId));
        return scanEventRepository.findByQrCodeId(qrCodeId);
    }

    private ShortCode generateUniqueShortCode() {
        ShortCode shortCode;
        do {
            shortCode = ShortCode.generate();
        } while (qrCodeRepository.existsByShortCode(shortCode));
        return shortCode;
    }

    @Transactional(readOnly = true)
    public QrCodeImageResponse getQrImage(UUID qrCodeId) {
        var qrCode = qrCodeRepository.findById(qrCodeId)
            .orElseThrow(() -> new IllegalArgumentException("QR code not found"));
        byte[] imageData = qrCodeGenerator.generate(qrCode);
        return new QrCodeImageResponse(
            imageData,
            "image/png",
            "qr-" + qrCodeId + ".png"
        );
    }
}
