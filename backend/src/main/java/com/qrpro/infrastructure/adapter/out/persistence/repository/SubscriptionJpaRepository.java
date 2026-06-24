package com.qrpro.infrastructure.adapter.out.persistence.repository;

import com.qrpro.infrastructure.adapter.out.persistence.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, UUID> {

    Optional<SubscriptionEntity> findByUserIdAndActiveTrue(UUID userId);

    @Query(value = """
        SELECT COUNT(*) FROM qr_codes 
        WHERE owner_id = :userId 
        AND created_at >= DATE_TRUNC('month', CURRENT_DATE)
        """, nativeQuery = true)
    long countQrCodesThisMonth(@Param("userId") UUID userId);

    @Query(value = """
        SELECT COUNT(*) FROM scan_events se
        JOIN qr_codes qr ON se.qr_code_id = qr.id
        WHERE qr.owner_id = :userId
        AND se.scanned_at >= DATE_TRUNC('month', CURRENT_DATE)
        """, nativeQuery = true)
    long countScansThisMonth(@Param("userId") UUID userId);
}
