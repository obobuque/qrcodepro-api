package com.qrpro.application.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ScanEventResponse(
    UUID id,
    String shortCode,
    String ipAddress,
    String userAgent,
    String referer,
    Instant scannedAt
) {}
