package com.qrpro.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ApiKey(
    UUID id,
    UUID userId,
    String keyHash,
    String name,
    OffsetDateTime lastUsedAt,
    OffsetDateTime createdAt,
    boolean active
) {
    public ApiKey markUsed() {
        return new ApiKey(id, userId, keyHash, name, OffsetDateTime.now(), createdAt, active);
    }

    public ApiKey deactivate() {
        return new ApiKey(id, userId, keyHash, name, lastUsedAt, createdAt, false);
    }
}
