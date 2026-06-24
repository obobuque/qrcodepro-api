package com.qrpro.infrastructure.adapter.in.web;

import com.qrpro.application.service.ApiKeyService;
import com.qrpro.application.service.ApiKeyService.ApiKeyGenerationResult;
import com.qrpro.domain.model.ApiKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/api-keys")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "API Keys", description = "Gerenciamento de chaves de API para integracao server-to-server")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    @Operation(summary = "Gerar nova API Key")
    public ResponseEntity<ApiKeyGenerationResult> generateKey(
            @RequestParam(required = false, defaultValue = "Default") String name) {
        UUID userId = currentUserId();
        ApiKeyGenerationResult result = apiKeyService.generateKey(userId, name);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    @Operation(summary = "Listar minhas API Keys")
    public ResponseEntity<List<ApiKeyResponse>> listKeys() {
        List<ApiKey> keys = apiKeyService.listByUser(currentUserId());
        return ResponseEntity.ok(keys.stream()
            .map(k -> new ApiKeyResponse(
                k.id(),
                k.name(),
                maskHash(k.keyHash()),
                k.lastUsedAt(),
                k.createdAt(),
                k.active()
            ))
            .toList());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Revogar API Key")
    public ResponseEntity<Void> revokeKey(@PathVariable UUID id) {
        apiKeyService.revokeKey(id, currentUserId());
        return ResponseEntity.noContent().build();
    }

    private UUID currentUserId() {
        return UUID.fromString(
            (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    private String maskHash(String hash) {
        if (hash == null || hash.length() < 12) return "***";
        return hash.substring(0, 6) + "..." + hash.substring(hash.length() - 6);
    }

    public record ApiKeyResponse(
        UUID id,
        String name,
        String keyHash,
        java.time.OffsetDateTime lastUsedAt,
        java.time.OffsetDateTime createdAt,
        boolean active
    ) {}
}
