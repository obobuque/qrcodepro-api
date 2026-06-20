package com.qrpro.domain.repository;

import com.qrpro.domain.model.ScanEvent;
import java.util.List;
import java.util.UUID;

public interface ScanEventRepositoryPort {
    ScanEvent save(ScanEvent scanEvent);
    List<ScanEvent> saveAll(List<ScanEvent> scanEvents);
    List<ScanEvent> findByQrCodeId(UUID qrCodeId);
    long countByQrCodeId(UUID qrCodeId);
}
