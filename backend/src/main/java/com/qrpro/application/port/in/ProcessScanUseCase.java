package com.qrpro.application.port.in;

import java.time.Instant;

public interface ProcessScanUseCase {
    ScanResult process(String shortCode, ScanMetadata metadata);

    record ScanMetadata(
        String ipAddress,
        String userAgent,
        String referer,
        Instant timestamp
    ) {}

    record ScanResult(
        String redirectUrl,
        boolean tracked
    ) {}
}
