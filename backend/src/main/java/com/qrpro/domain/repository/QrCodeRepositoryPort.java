package com.qrpro.domain.repository;

import com.qrpro.domain.model.QrCode;
import com.qrpro.domain.vo.ShortCode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QrCodeRepositoryPort {
    QrCode save(QrCode qrCode);
    Optional<QrCode> findById(UUID id);
    Optional<QrCode> findByShortCode(ShortCode shortCode);
    Optional<QrCode> findByShortCode(String shortCode);
    boolean existsByShortCode(ShortCode shortCode);
    void deleteById(UUID id);
    List<QrCode> findByOwnerId(UUID ownerId);
}
