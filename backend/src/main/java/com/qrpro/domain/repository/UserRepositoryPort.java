package com.qrpro.domain.repository;

import com.qrpro.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    User save(User user);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    java.util.Optional<User> findById(java.util.UUID id);
    java.util.Optional<User> findByApiKey(String apiKey);
    User updateApiKey(java.util.UUID userId, String apiKey);
}
