package com.qrpro.domain.repository;

import com.qrpro.domain.model.Subscription;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepositoryPort {
    Optional<Subscription> findActiveByUserId(UUID userId);
    Subscription save(Subscription subscription);
    long countQrCodesThisMonth(UUID userId);
    long countScansThisMonth(UUID userId);
}
