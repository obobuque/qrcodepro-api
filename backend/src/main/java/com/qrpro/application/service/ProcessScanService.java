package com.qrpro.application.service;

import com.qrpro.application.port.in.ProcessScanUseCase;
import com.qrpro.application.port.out.QrCodeCachePort;
import com.qrpro.application.port.out.ScanEventIngestionPort;
import com.qrpro.domain.model.ScanEvent;
import com.qrpro.domain.repository.QrCodeRepositoryPort;
import com.qrpro.domain.vo.ShortCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessScanService implements ProcessScanUseCase {

    private final QrCodeRepositoryPort qrCodeRepository;
    private final QrCodeCachePort cachePort;
    private final ScanEventIngestionPort scanBuffer;

    @Override
    @Transactional(readOnly = true)
    public ScanResult process(String shortCodeStr, ScanMetadata metadata) {
        try {
            var shortCode = new ShortCode(shortCodeStr);

            String cachedUrl = cachePort.getDestination(shortCode);
            if (cachedUrl != null) {
                fireTrackingEvent(shortCode, metadata);
                return new ScanResult(cachedUrl, true);
            }

            var qrCode = qrCodeRepository.findByShortCode(shortCode)
                .orElse(null);

            if (qrCode == null) {
                log.warn("QR code not found for short code: {}", shortCodeStr);
                return new ScanResult(null, false);
            }

            if (!qrCode.isActive()) {
                log.warn("QR code is inactive: {}", shortCodeStr);
                return new ScanResult(null, false);
            }

            if (qrCode.isExpired()) {
                log.warn("QR code has expired: {}", shortCodeStr);
                return new ScanResult(null, false);
            }

            String targetUrl = qrCode.resolveTargetUrl();
            if (targetUrl == null) {
                log.error("QR code has no target URL: {}", shortCodeStr);
                return new ScanResult(null, false);
            }

            cachePort.cacheDestination(shortCode, targetUrl);
            fireTrackingEvent(shortCode, metadata);

            return new ScanResult(targetUrl, true);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid short code format: {}", shortCodeStr);
            return new ScanResult(null, false);
        }
    }

    private void fireTrackingEvent(ShortCode shortCode, ScanMetadata metadata) {
        try {
            var event = ScanEvent.create(
                UUID.randomUUID(),
                shortCode,
                metadata.ipAddress(),
                metadata.userAgent(),
                metadata.referer(),
                metadata.timestamp()
            );
            scanBuffer.ingest(event);
        } catch (Exception e) {
            log.error("Failed to ingest scan event", e);
        }
    }
}
