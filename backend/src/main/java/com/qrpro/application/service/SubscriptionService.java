package com.qrpro.application.service;

import com.qrpro.domain.model.Plan;
import com.qrpro.domain.model.Subscription;
import com.qrpro.domain.repository.SubscriptionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final SubscriptionRepositoryPort subscriptionRepository;

    public SubscriptionService(SubscriptionRepositoryPort subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription getOrCreateFreeSubscription(UUID userId) {
        return subscriptionRepository.findActiveByUserId(userId)
            .orElseGet(() -> {
                Subscription free = new Subscription(
                    null, userId, Plan.FREE,
                    java.time.OffsetDateTime.now(), null,
                    true, null, null
                );
                return subscriptionRepository.save(free);
            });
    }

    public Plan getUserPlan(UUID userId) {
        return subscriptionRepository.findActiveByUserId(userId)
            .map(Subscription::plan)
            .orElse(Plan.FREE);
    }

    public boolean canCreateQrCode(UUID userId) {
        Plan plan = getUserPlan(userId);
        long currentCount = subscriptionRepository.countQrCodesThisMonth(userId);
        return currentCount < plan.getMaxQrCodes();
    }

    public boolean canUseDynamicQr(UUID userId) {
        return getUserPlan(userId).isAllowsDynamic();
    }

    public boolean canReceiveScan(UUID userId) {
        Plan plan = getUserPlan(userId);
        long currentScans = subscriptionRepository.countScansThisMonth(userId);
        return currentScans < plan.getMaxScansPerMonth();
    }
}
