package com.qrpro.infrastructure.adapter.in.web;

import com.qrpro.domain.exception.PlanLimitsExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlanLimitsExceededException.class)
    public ResponseEntity<Map<String, Object>> handlePlanLimitsExceeded(PlanLimitsExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 429,
                "error", "Plan Limit Exceeded",
                "message", ex.getMessage(),
                "planId", ex.getPlanId(),
                "limitType", ex.getLimitType(),
                "upgradeUrl", ex.getUpgradeUrl()
            ));
    }
}
