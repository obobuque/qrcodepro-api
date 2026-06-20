package com.qrpro.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record User(
    UUID id,
    String username,
    String email,
    String password,
    boolean active,
    OffsetDateTime createdAt
) {}
