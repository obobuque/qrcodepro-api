package com.qrpro.application.event;

import java.time.Instant;
import java.util.UUID;

public record QrCodeGeneratedEvent(
    UUID qrCodeId,
    UUID ownerId,
    String type,
    String imageUrl,
    Instant generatedAt
) {}
