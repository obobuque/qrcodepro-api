package com.qrpro.infrastructure.adapter.out.persistence;

import com.qrpro.infrastructure.adapter.out.persistence.entity.ApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaApiKeyRepository extends JpaRepository<ApiKeyEntity, UUID> {
    Optional<ApiKeyEntity> findByKeyHash(String keyHash);
}
