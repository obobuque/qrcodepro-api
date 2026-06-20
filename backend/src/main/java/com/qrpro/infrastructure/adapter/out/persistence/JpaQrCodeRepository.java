package com.qrpro.infrastructure.adapter.out.persistence;

import com.qrpro.infrastructure.adapter.out.persistence.entity.QrCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaQrCodeRepository extends JpaRepository<QrCodeEntity, UUID> {
    Optional<QrCodeEntity> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
    List<QrCodeEntity> findByOwnerIdOrderByCreatedAtDesc(UUID ownerId);
}
