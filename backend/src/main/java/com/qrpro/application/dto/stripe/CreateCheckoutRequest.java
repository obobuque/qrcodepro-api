package com.qrpro.application.dto.stripe;

public record CreateCheckoutRequest(
    String planId,  // starter, pro, business
    String successUrl,
    String cancelUrl
) {}
