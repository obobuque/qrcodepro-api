package com.qrpro.unit.application;

import com.qrpro.application.port.out.TokenGeneratorPort;
import com.qrpro.application.service.AuthService;
import com.qrpro.domain.model.User;
import com.qrpro.domain.repository.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock private TokenGeneratorPort tokenGeneratorPort;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private AuthService authService;

    private static final String USERNAME = "gabriel";
    private static final String EMAIL = "gabriel@email.com";
    private static final String PASSWORD = "senha123";
    private static final String ENCODED = "$2a$encoded";
    private static final String TOKEN = "jwt.token.here";

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(any())).thenReturn(ENCODED);
        when(tokenGeneratorPort.generateToken(any(), any())).thenReturn(TOKEN);
    }

    @Test
    void register_should_return_token_for_new_user() {
        when(userRepositoryPort.existsByEmail(EMAIL)).thenReturn(false);
        when(userRepositoryPort.existsByUsername(USERNAME)).thenReturn(false);
        when(userRepositoryPort.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new User(u.id(), u.username(), u.email(), u.password(), u.active(), null, null);
        });

        String token = authService.register(USERNAME, EMAIL, PASSWORD);

        assertThat(token).isEqualTo(TOKEN);
        verify(userRepositoryPort).save(any(User.class));
        verify(passwordEncoder).encode(PASSWORD);
    }

    @Test
    void register_should_throw_when_email_already_exists() {
        when(userRepositoryPort.existsByEmail(EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> authService.register(USERNAME, EMAIL, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    void register_should_throw_when_username_already_exists() {
        when(userRepositoryPort.existsByEmail(EMAIL)).thenReturn(false);
        when(userRepositoryPort.existsByUsername(USERNAME)).thenReturn(true);

        assertThatThrownBy(() -> authService.register(USERNAME, EMAIL, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");

        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    void login_should_return_token_for_valid_credentials() {
        User stored = new User(UUID.randomUUID(), USERNAME, EMAIL, ENCODED, true, null, null);
        when(userRepositoryPort.findByUsername(USERNAME)).thenReturn(Optional.of(stored));
        when(passwordEncoder.matches(PASSWORD, ENCODED)).thenReturn(true);

        AuthService.LoginResult result = authService.login(USERNAME, PASSWORD);

        assertThat(result.token()).isEqualTo(TOKEN);
        assertThat(result.username()).isEqualTo(USERNAME);
        assertThat(result.email()).isEqualTo(EMAIL);
    }

    @Test
    void login_should_throw_when_user_not_found() {
        when(userRepositoryPort.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(USERNAME, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void login_should_throw_for_wrong_password() {
        User stored = new User(UUID.randomUUID(), USERNAME, EMAIL, ENCODED, true, null, null);
        when(userRepositoryPort.findByUsername(USERNAME)).thenReturn(Optional.of(stored));
        when(passwordEncoder.matches(PASSWORD, ENCODED)).thenReturn(false);

        assertThatThrownBy(() -> authService.login(USERNAME, PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid password");
    }
}
