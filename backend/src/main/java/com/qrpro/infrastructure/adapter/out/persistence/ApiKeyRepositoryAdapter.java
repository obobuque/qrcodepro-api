package com.qrpro.infrastructure.adapter.out.persistence;

import com.qrpro.domain.model.ApiKey;
import com.qrpro.domain.repository.ApiKeyRepositoryPort;
import com.qrpro.infrastructure.adapter.out.persistence.entity.ApiKeyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiKeyRepositoryAdapter implements ApiKeyRepositoryPort {

    private final JpaApiKeyRepository jpaRepository;

    @Override
    public ApiKey save(ApiKey apiKey) {
        ApiKeyEntity entity = toEntity(apiKey);
        ApiKeyEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ApiKey> findByKeyHash(String keyHash) {
        return jpaRepository.findByKeyHash(keyHash).map(this::toDomain);
    }

    @Override
    public List<ApiKey> findByUserId(UUID userId) {
        return jpaRepository.findAll().stream()
            .filter(e -> e.getUserId().equals(userId))
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ApiKeyEntity toEntity(ApiKey apiKey) {
        return ApiKeyEntity.builder()
            .id(apiKey.id())
            .userId(apiKey.userId())
            .keyHash(apiKey.keyHash())
            .name(apiKey.name())
            .lastUsedAt(apiKey.lastUsedAt())
            .createdAt(apiKey.createdAt())
            .active(apiKey.active())
            .build();
    }

    private ApiKey toDomain(ApiKeyEntity entity) {
        return new ApiKey(
            entity.getId(),
            entity.getUserId(),
            entity.getKeyHash(),
            entity.getName(),
            entity.getLastUsedAt(),
            entity.getCreatedAt(),
            entity.isActive()
        );
    }
}
