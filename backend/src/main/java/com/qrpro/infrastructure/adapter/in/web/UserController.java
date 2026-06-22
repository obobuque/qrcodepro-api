package com.qrpro.infrastructure.adapter.in.web;

import com.qrpro.application.dto.response.UserResponse;
import com.qrpro.domain.repository.UserRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Users", description = "Perfil e API Key do usuário autenticado")
@RequiredArgsConstructor
public class UserController {

    private final UserRepositoryPort userRepositoryPort;

    @GetMapping("/me")
    @Operation(summary = "Obter perfil do usuário logado")
    public ResponseEntity<UserResponse> me() {
        UUID userId = currentUserId();
        var user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(new UserResponse(
                user.id(), user.username(), user.email(),
                user.active(), user.createdAt()));
    }

    @PostMapping("/me/api-key")
    @Operation(summary = "Gerar ou regenerar API Key")
    public ResponseEntity<Map<String, String>> generateApiKey() {
        UUID userId = currentUserId();
        String apiKey = "qrpro_" + generateSecureToken();
        userRepositoryPort.updateApiKey(userId, apiKey);
        return ResponseEntity.ok(Map.of(
                "apiKey", apiKey,
                "hint", "Use no header: X-API-Key: " + apiKey
        ));
    }

    private UUID currentUserId() {
        return UUID.fromString(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
