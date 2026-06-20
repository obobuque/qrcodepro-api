package com.qrpro.infrastructure.adapter.out.persistence;

import com.qrpro.domain.model.ScanEvent;
import com.qrpro.domain.repository.ScanEventRepositoryPort;
import com.qrpro.domain.vo.ShortCode;
import com.qrpro.infrastructure.adapter.out.persistence.entity.ScanEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScanEventRepositoryAdapter implements ScanEventRepositoryPort {

    private final JpaScanEventRepository jpaRepository;

    @Override
    public ScanEvent save(ScanEvent scanEvent) {
        ScanEventEntity entity = toEntity(scanEvent);
        ScanEventEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<ScanEvent> saveAll(List<ScanEvent> scanEvents) {
        List<ScanEventEntity> entities = scanEvents.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
        
        List<ScanEventEntity> saved = jpaRepository.saveAll(entities);
        
        return saved.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<ScanEvent> findByQrCodeId(UUID qrCodeId) {
        return jpaRepository.findByQrCodeId(qrCodeId).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countByQrCodeId(UUID qrCodeId) {
        return jpaRepository.countByQrCodeId(qrCodeId);
    }

    private ScanEventEntity toEntity(ScanEvent event) {
        return ScanEventEntity.builder()
            .id(event.getId())
            .qrCodeId(event.getQrCodeId())
            .shortCode(event.getShortCode() != null ? event.getShortCode().value() : null)
            .ipAddress(event.getIpAddress())
            .userAgent(event.getUserAgent())
            .referer(event.getReferer())
            .scannedAt(event.getScannedAt())
            .build();
    }

    private ScanEvent toDomain(ScanEventEntity entity) {
        return ScanEvent.create(
            entity.getQrCodeId(),
            entity.getShortCode() != null ? new ShortCode(entity.getShortCode()) : null,
            entity.getIpAddress(),
            entity.getUserAgent(),
            entity.getReferer(),
            entity.getScannedAt()
        );
    }
}
