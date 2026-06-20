package com.qrpro.infrastructure.adapter.out.persistence;

import com.qrpro.domain.model.QrCode;
import com.qrpro.domain.repository.QrCodeRepositoryPort;
import com.qrpro.domain.vo.ShortCode;
import com.qrpro.infrastructure.adapter.out.persistence.entity.QrCodeEntity;
import com.qrpro.infrastructure.adapter.out.persistence.mapper.QrCodeEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QrCodeRepositoryAdapter implements QrCodeRepositoryPort {

    private final JpaQrCodeRepository jpaRepository;
    private final QrCodeEntityMapper mapper;

    @Override
    public QrCode save(QrCode qrCode) {
        QrCodeEntity entity = mapper.toEntity(qrCode);
        QrCodeEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<QrCode> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<QrCode> findByShortCode(ShortCode shortCode) {
        return jpaRepository.findByShortCode(shortCode.value())
            .map(mapper::toDomain);
    }

    @Override
    public Optional<QrCode> findByShortCode(String shortCode) {
        return jpaRepository.findByShortCode(shortCode)
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByShortCode(ShortCode shortCode) {
        return jpaRepository.existsByShortCode(shortCode.value());
    }

    @Override
    public List<com.qrpro.domain.model.QrCode> findByOwnerId(java.util.UUID ownerId) {
        return jpaRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Transactional
    public void updateDestinationUrl(UUID id, String newUrl) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.setDestinationUrl(newUrl);
            entity.setUpdatedAt(java.time.Instant.now());
            jpaRepository.save(entity);
        });
    }
}
