package com.qrpro.application.port.out;

import java.util.UUID;

public interface TokenGeneratorPort {
    String generateToken(UUID userId, String email);
}
