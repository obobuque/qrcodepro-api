package com.qrpro.application.service;

import com.qrpro.domain.model.QrCode;
import com.qrpro.domain.model.User;
import com.qrpro.domain.repository.QrCodeRepositoryPort;
import com.qrpro.domain.service.QrCodeGenerator;
import com.qrpro.application.dto.qr.CreateStaticQrRequest;
import com.qrpro.application.dto.qr.CreateDynamicQrRequest;
import com.qrpro.application.dto.qr.QrCodeResponse;
import com.qrpro.application.mapper.QrCodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class QrCodeService {

    private final QrCodeRepositoryPort qrCodeRepository;
    private final QrCodeGenerator qrCodeGenerator;
    private final QrCodeMapper qrCodeMapper;
    private final PlanEnforcementService planEnforcement;

    public QrCodeService(QrCodeRepositoryPort qrCodeRepository,
                         QrCodeGenerator qrCodeGenerator,
                         QrCodeMapper qrCodeMapper,
                         PlanEnforcementService planEnforcement) {
        this.qrCodeRepository = qrCodeRepository;
        this.qrCodeGenerator = qrCodeGenerator;
        this.qrCodeMapper = qrCodeMapper;
        this.planEnforcement = planEnforcement;
    }

    public QrCodeResponse createStaticQr(UUID userId, CreateStaticQrRequest request) {
        planEnforcement.enforceQrCreation(userId);

        QrCode qrCode = qrCodeGenerator.generateStatic(
            userId,
            request.content(),
            request.size(),
            request.foregroundColor(),
            request.backgroundColor()
        );
        QrCode saved = qrCodeRepository.save(qrCode);
        return qrCodeMapper.toResponse(saved);
    }

    public QrCodeResponse createDynamicQr(UUID userId, CreateDynamicQrRequest request) {
        planEnforcement.enforceQrCreation(userId);
        planEnforcement.enforceDynamicQr(userId);

        QrCode qrCode = qrCodeGenerator.generateDynamic(
            userId,
            request.destinationUrl(),
            request.size()
        );
        QrCode saved = qrCodeRepository.save(qrCode);
        return qrCodeMapper.toResponse(saved);
    }

    public List<QrCodeResponse> listByOwner(UUID userId) {
        return qrCodeRepository.findByOwnerId(userId).stream()
            .map(qrCodeMapper::toResponse)
            .toList();
    }

    public QrCodeResponse getById(UUID id, UUID userId) {
        QrCode qrCode = qrCodeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("QR Code not found"));
        if (!qrCode.ownerId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        return qrCodeMapper.toResponse(qrCode);
    }

    public void delete(UUID id, UUID userId) {
        QrCode qrCode = qrCodeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("QR Code not found"));
        if (!qrCode.ownerId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        qrCodeRepository.deleteById(id);
    }
}
