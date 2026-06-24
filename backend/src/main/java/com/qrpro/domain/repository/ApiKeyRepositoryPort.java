package com.qrpro.domain.repository;
import com.qrpro.domain.model.ApiKey;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface ApiKeyRepositoryPort {
    ApiKey save(ApiKey apiKey);
    Optional<ApiKey> findByKeyHash(String keyHash);
    List<ApiKey> findByUserId(UUID userId);
    void deleteById(UUID id);
}
