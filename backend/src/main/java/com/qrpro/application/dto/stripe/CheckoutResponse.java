package com.qrpro.application.dto.stripe;

public record CheckoutResponse(
    String sessionId,
    String checkoutUrl
) {}
