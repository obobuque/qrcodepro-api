package com.qrpro.application.service;

import com.qrpro.application.dto.stripe.CheckoutResponse;
import com.qrpro.application.dto.stripe.CreateCheckoutRequest;
import com.qrpro.domain.model.Plan;
import com.qrpro.domain.model.Subscription;
import com.qrpro.domain.repository.SubscriptionRepositoryPort;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StripeService {

    private final SubscriptionRepositoryPort subscriptionRepository;
    private final SubscriptionService subscriptionService;

    @Value("${qr.base-url:https://qrcodepro-api.onrender.com}")
    private String baseUrl;

    public StripeService(SubscriptionRepositoryPort subscriptionRepository,
                        SubscriptionService subscriptionService) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionService = subscriptionService;
    }

    public CheckoutResponse createCheckoutSession(UUID userId, CreateCheckoutRequest request) {
        Plan plan = Plan.fromId(request.planId());

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(request.successUrl())
                .setCancelUrl(request.cancelUrl())
                .setClientReferenceId(userId.toString())
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount((long) plan.getPriceCents())
                                .setRecurring(
                                    SessionCreateParams.LineItem.PriceData.Recurring.builder()
                                        .setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                                        .build()
                                )
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("QR Pro - " + plan.getDisplayName())
                                        .setDescription(plan.getMaxQrCodes() + " QR codes / month")
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();

            Session session = Session.create(params);

            return new CheckoutResponse(session.getId(), session.getUrl());

        } catch (Exception e) {
            throw new RuntimeException("Failed to create checkout session: " + e.getMessage(), e);
        }
    }

    public void handleSubscriptionCreated(String stripeSubscriptionId, String stripeCustomerId, 
                                         UUID userId, String planId) {
        // Desativar assinatura atual
        subscriptionRepository.findActiveByUserId(userId)
            .ifPresent(sub -> {
                // Criar nova assinatura ativa
            });

        // Criar nova assinatura
        Subscription newSub = new Subscription(
            null,
            userId,
            Plan.fromId(planId),
            java.time.OffsetDateTime.now(),
            null,
            true,
            stripeSubscriptionId,
            stripeCustomerId
        );
        subscriptionRepository.save(newSub);
    }

    public void handleSubscriptionDeleted(String stripeSubscriptionId) {
        // Encontrar e desativar assinatura
        // Implementar busca por stripeSubscriptionId
    }
}
