package com.qrpro.infrastructure.adapter.out.persistence;

import com.qrpro.domain.model.Plan;
import com.qrpro.domain.model.Subscription;
import com.qrpro.domain.repository.SubscriptionRepositoryPort;
import com.qrpro.infrastructure.adapter.out.persistence.entity.SubscriptionEntity;
import com.qrpro.infrastructure.adapter.out.persistence.repository.SubscriptionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SubscriptionRepositoryAdapter implements SubscriptionRepositoryPort {

    private final SubscriptionJpaRepository jpaRepository;

    public SubscriptionRepositoryAdapter(SubscriptionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Subscription> findActiveByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndActiveTrue(userId)
            .map(this::toDomain);
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = toEntity(subscription);
        SubscriptionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public long countQrCodesThisMonth(UUID userId) {
        return jpaRepository.countQrCodesThisMonth(userId);
    }

    @Override
    public long countScansThisMonth(UUID userId) {
        return jpaRepository.countScansThisMonth(userId);
    }

    private Subscription toDomain(SubscriptionEntity entity) {
        return new Subscription(
            entity.getId(),
            entity.getUserId(),
            Plan.fromId(entity.getPlanId()),
            entity.getStartedAt(),
            entity.getExpiresAt(),
            entity.isActive(),
            entity.getStripeSubscriptionId(),
            entity.getStripeCustomerId()
        );
    }

    private SubscriptionEntity toEntity(Subscription domain) {
        SubscriptionEntity entity = new SubscriptionEntity();
        entity.setId(domain.id());
        entity.setUserId(domain.userId());
        entity.setPlanId(domain.plan().getId());
        entity.setStartedAt(domain.startedAt());
        entity.setExpiresAt(domain.expiresAt());
        entity.setActive(domain.active());
        entity.setStripeSubscriptionId(domain.stripeSubscriptionId());
        entity.setStripeCustomerId(domain.stripeCustomerId());
        return entity;
    }
}
