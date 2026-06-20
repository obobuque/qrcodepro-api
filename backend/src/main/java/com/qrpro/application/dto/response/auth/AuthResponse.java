package com.qrpro.application.dto.response.auth;

public record AuthResponse(
    String token,
    String type,
    String username,
    String email
) {}
