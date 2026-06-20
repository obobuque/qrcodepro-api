package com.qrpro.infrastructure.adapter.out.persistence;

import com.qrpro.infrastructure.adapter.out.persistence.entity.ScanEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaScanEventRepository extends JpaRepository<ScanEventEntity, UUID> {
    List<ScanEventEntity> findByQrCodeId(UUID qrCodeId);
    long countByQrCodeId(UUID qrCodeId);
}
