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

    @Override public ApiKey save(ApiKey apiKey) { return toDomain(jpaRepository.save(toEntity(apiKey))); }
    @Override public Optional<ApiKey> findByKeyHash(String keyHash) { return jpaRepository.findByKeyHash(keyHash).map(this::toDomain); }
    @Override public List<ApiKey> findByUserId(UUID userId) { return jpaRepository.findAll().stream().filter(e -> e.getUserId().equals(userId)).map(this::toDomain).collect(Collectors.toList()); }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }

    private ApiKeyEntity toEntity(ApiKey k) { return ApiKeyEntity.builder().id(k.id()).userId(k.userId()).keyHash(k.keyHash()).name(k.name()).lastUsedAt(k.lastUsedAt()).createdAt(k.createdAt()).active(k.active()).build(); }
    private ApiKey toDomain(ApiKeyEntity e) { return new ApiKey(e.getId(), e.getUserId(), e.getKeyHash(), e.getName(), e.getLastUsedAt(), e.getCreatedAt(), e.isActive()); }
}
