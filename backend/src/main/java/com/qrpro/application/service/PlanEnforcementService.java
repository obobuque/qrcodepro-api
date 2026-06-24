package com.qrpro.application.service;

import com.qrpro.domain.exception.PlanLimitsExceededException;
import com.qrpro.domain.model.Plan;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlanEnforcementService {

    private final SubscriptionService subscriptionService;

    public PlanEnforcementService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void enforceQrCreation(UUID userId) {
        if (!subscriptionService.canCreateQrCode(userId)) {
            Plan plan = subscriptionService.getUserPlan(userId);
            throw new PlanLimitsExceededException(
                plan.getId(),
                "max_qr_codes",
                "/upgrade?from=" + plan.getId()
            );
        }
    }

    public void enforceDynamicQr(UUID userId) {
        if (!subscriptionService.canUseDynamicQr(userId)) {
            Plan plan = subscriptionService.getUserPlan(userId);
            throw new PlanLimitsExceededException(
                plan.getId(),
                "dynamic_qr_not_allowed",
                "/upgrade?from=" + plan.getId() + "&feature=dynamic"
            );
        }
    }

    public void enforceScan(UUID userId) {
        if (!subscriptionService.canReceiveScan(userId)) {
            Plan plan = subscriptionService.getUserPlan(userId);
            throw new PlanLimitsExceededException(
                plan.getId(),
                "max_scans",
                "/upgrade?from=" + plan.getId()
            );
        }
    }
}
