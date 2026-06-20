package com.qrpro.application.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    boolean active,
    OffsetDateTime createdAt
) {}
