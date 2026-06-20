package com.qrpro.application.service;

import com.qrpro.application.port.out.TokenGeneratorPort;
import com.qrpro.domain.model.User;
import com.qrpro.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenGeneratorPort tokenGeneratorPort;
    private final PasswordEncoder passwordEncoder;
    private final UserRepositoryPort userRepositoryPort;

    @Transactional
    public String register(String username, String email, String password) {
        if (userRepositoryPort.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepositoryPort.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = new User(UUID.randomUUID(), username, email,
                passwordEncoder.encode(password), true, null);

        User saved = userRepositoryPort.save(user);
        return tokenGeneratorPort.generateToken(saved.id(), saved.email());
    }

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(password, user.password())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return tokenGeneratorPort.generateToken(user.id(), user.email());
    }
}
