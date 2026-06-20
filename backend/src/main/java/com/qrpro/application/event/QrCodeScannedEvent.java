package com.qrpro.application.event;

import java.time.Instant;

public record QrCodeScannedEvent(
    String shortCode,
    String ipAddress,
    String userAgent,
    Instant scannedAt
) {}
