package com.qrpro.application.dto.response;

import java.time.Instant;

public record QrCodeResponse(
    String id,
    String type,
    String shortCode,
    String content,
    String destinationUrl,
    String imageUrl,
    String foregroundColor,
    String backgroundColor,
    String dotStyle,
    String errorCorrectionLevel,
    int sizePixels,
    boolean active,
    long scanCount,
    Instant createdAt,
    Instant expiresAt
) {}
