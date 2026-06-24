// Force rebuild - cache cleared
package com.qrpro.infrastructure.adapter.in.web;

import com.qrpro.application.dto.stripe.CheckoutResponse;
import com.qrpro.application.dto.stripe.CreateCheckoutRequest;
import com.qrpro.application.service.StripeService;
import com.qrpro.infrastructure.config.StripeConfig;
import com.qrpro.infrastructure.security.jwt.JwtTokenProvider;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;
    private final StripeConfig stripeConfig;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> createCheckout(
            @RequestBody CreateCheckoutRequest request,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);
        
        CheckoutResponse response = stripeService.createCheckoutSession(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhooks/stripe")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request) {
        String payload = readRequestBody(request);
        String sigHeader = request.getHeader("Stripe-Signature");
        
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
            
            log.info("Stripe webhook received: {}", event.getType());
            
            switch (event.getType()) {
                case "checkout.session.completed" -> {
                    Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
                    String clientRef = session.getClientReferenceId();
                    UUID userId = (clientRef != null && !clientRef.isEmpty()) ? UUID.fromString(clientRef) : null;
                    if (userId == null) {
                        log.warn("No clientReferenceId in session {}", session.getId());
                        return ResponseEntity.badRequest().body("No clientReferenceId");
                    }
                    String planId = extractPlanFromSession(session);
                    stripeService.handleSubscriptionCreated(
                        session.getSubscription(),
                        session.getCustomer(),
                        userId,
                        planId
                    );
                }
                case "customer.subscription.deleted" -> {
                    // Handle cancellation
                }
                case "invoice.paid" -> {
                    // Handle recurring payment
                }
            }
            
            return ResponseEntity.ok("Webhook processed");
            
        } catch (Exception e) {
            log.error("Webhook error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }
    }

    private String readRequestBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            log.error("Error reading request body", e);
        }
        return sb.toString();
    }

    private String extractPlanFromSession(Session session) {
        long amount = session.getAmountTotal() != null ? session.getAmountTotal() : 0;
        return switch ((int) amount) {
            case 900 -> "starter";
            case 2900 -> "pro";
            case 9900 -> "business";
            default -> "starter";
        };
    }
}

