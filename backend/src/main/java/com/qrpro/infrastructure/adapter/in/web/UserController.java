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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Users", description = "Perfil do usuário autenticado")
@RequiredArgsConstructor
public class UserController {

    private final UserRepositoryPort userRepositoryPort;

    @GetMapping("/me")
    @Operation(summary = "Obter perfil do usuário logado")
    public ResponseEntity<UserResponse> me() {
        UUID userId = UUID.fromString(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        var user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ResponseEntity.ok(new UserResponse(
                user.id(), user.username(), user.email(),
                user.active(), user.createdAt()));
    }
}
