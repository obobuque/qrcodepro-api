package com.qrpro.infrastructure.adapter.out.persistence;

import com.qrpro.domain.model.User;
import com.qrpro.domain.repository.UserRepositoryPort;
import com.qrpro.infrastructure.adapter.out.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.builder()
                .id(user.id())
                .username(user.username())
                .email(user.email())
                .password(user.password())
                .active(user.active())
                .build();
        return toDomain(userRepository.save(entity));
    }

    @Override
    public java.util.Optional<com.qrpro.domain.model.User> findById(java.util.UUID id) {
        return userRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}
