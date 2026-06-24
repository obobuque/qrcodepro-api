package com.qrpro.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Subscription(
    UUID id,
    UUID userId,
    Plan plan,
    OffsetDateTime startedAt,
    OffsetDateTime expiresAt,
    boolean active,
    String stripeSubscriptionId,
    String stripeCustomerId
) {
    public boolean isValid() {
        if (!active) return false;
        if (expiresAt == null) return true;
        return OffsetDateTime.now().isBefore(expiresAt);
    }
}
