package com.qrpro.unit.application;

import com.qrpro.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String SECRET =
            "test-secret-key-must-be-at-least-64-bytes-long-for-hs512-algorithm-ok";
    private static final long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET, EXPIRATION);
    }

    @Test
    void generateToken_should_return_non_null_token() {
        UUID userId = UUID.randomUUID();
        String token = jwtTokenProvider.generateToken(userId, "test@email.com");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void generateToken_should_have_three_parts() {
        String token = jwtTokenProvider.generateToken(UUID.randomUUID(), "test@email.com");
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void validateToken_should_return_true_for_valid_token() {
        String token = jwtTokenProvider.generateToken(UUID.randomUUID(), "test@email.com");
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_should_return_false_for_garbage() {
        assertThat(jwtTokenProvider.validateToken("not.a.token")).isFalse();
    }

    @Test
    void validateToken_should_return_false_for_empty() {
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
    }

    @Test
    void validateToken_should_return_false_for_tampered_token() {
        String token = jwtTokenProvider.generateToken(UUID.randomUUID(), "test@email.com");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertThat(jwtTokenProvider.validateToken(tampered)).isFalse();
    }

    @Test
    void getUserIdFromToken_should_return_correct_userId() {
        UUID userId = UUID.randomUUID();
        String token = jwtTokenProvider.generateToken(userId, "test@email.com");
        assertThat(jwtTokenProvider.getUserIdFromToken(token))
                .isEqualTo(userId.toString());
    }

    @Test
    void tokens_for_different_users_should_differ() {
        String token1 = jwtTokenProvider.generateToken(UUID.randomUUID(), "a@email.com");
        String token2 = jwtTokenProvider.generateToken(UUID.randomUUID(), "b@email.com");
        assertThat(token1).isNotEqualTo(token2);
    }
}
