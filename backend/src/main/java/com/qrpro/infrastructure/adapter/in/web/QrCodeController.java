package com.qrpro.infrastructure.adapter.in.web;

import com.qrpro.application.dto.request.GenerateDynamicQrRequest;
import com.qrpro.application.dto.request.GenerateStaticQrRequest;
import com.qrpro.application.dto.request.UpdateDestinationRequest;
import com.qrpro.application.dto.response.QrCodeResponse;
import com.qrpro.application.dto.response.ScanEventResponse;
import com.qrpro.application.mapper.QrCodeMapper;
import com.qrpro.application.port.in.GenerateQrCodeUseCase;
import com.qrpro.application.port.in.UpdateDynamicDestinationUseCase;
import com.qrpro.application.service.QrCodeService;
import com.qrpro.domain.model.ScanEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/qr")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "QR Codes", description = "Endpoints protegidos — requer Bearer token")
@RequiredArgsConstructor
public class QrCodeController {

    private final GenerateQrCodeUseCase generateQrCodeUseCase;
    private final UpdateDynamicDestinationUseCase updateDestinationUseCase;
    private final QrCodeService qrCodeService;
    private final QrCodeMapper qrCodeMapper;

    @PostMapping("/static")
    @Operation(summary = "Criar QR Code estático")
    public ResponseEntity<QrCodeResponse> generateStatic(
            @Valid @RequestBody GenerateStaticQrRequest request) {
        var command = new GenerateQrCodeUseCase.StaticQrCommand(
                currentUserId().toString(), request.content(), request.design());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qrCodeMapper.toResponse(generateQrCodeUseCase.generateStatic(command)));
    }

    @PostMapping("/dynamic")
    @Operation(summary = "Criar QR Code dinâmico")
    public ResponseEntity<QrCodeResponse> generateDynamic(
            @Valid @RequestBody GenerateDynamicQrRequest request) {
        var command = new GenerateQrCodeUseCase.DynamicQrCommand(
                currentUserId().toString(), request.initialUrl(), request.design());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qrCodeMapper.toResponse(generateQrCodeUseCase.generateDynamic(command)));
    }

    @GetMapping
    @Operation(summary = "Listar meus QR Codes")
    public ResponseEntity<List<QrCodeResponse>> listMine() {
        List<QrCodeResponse> result = qrCodeService.listByOwner(currentUserId())
                .stream().map(qrCodeMapper::toResponse).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar QR Code por ID")
    public ResponseEntity<QrCodeResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(qrCodeMapper.toResponse(qrCodeService.getById(id)));
    }

    @GetMapping("/{id}/image")
    @Operation(summary = "Obter imagem do QR Code")
    public ResponseEntity<byte[]> getQrImage(@PathVariable UUID id) {
        var img = qrCodeService.getQrImage(id);
        return ResponseEntity.ok()
                .header("Content-Type", img.contentType())
                .header("Content-Disposition", "inline; filename=\"" + img.fileName() + "\"")
                .body(img.imageData());
    }

    @GetMapping("/{id}/scans")
    @Operation(summary = "Histórico de scans do QR Code")
    public ResponseEntity<List<ScanEventResponse>> getScans(@PathVariable UUID id) {
        List<ScanEventResponse> scans = qrCodeService.getScanHistory(id)
                .stream().map(this::toScanResponse).toList();
        return ResponseEntity.ok(scans);
    }

    @PutMapping("/{id}/destination")
    @Operation(summary = "Atualizar destino do QR Code dinâmico")
    public ResponseEntity<QrCodeResponse> updateDestination(
            @PathVariable String id,
            @Valid @RequestBody UpdateDestinationRequest request) {
        var command = new UpdateDynamicDestinationUseCase.UpdateDestinationCommand(request.newUrl());
        return ResponseEntity.ok(qrCodeMapper.toResponse(
                updateDestinationUseCase.updateDestination(id, command)));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desativar QR Code")
    public ResponseEntity<QrCodeResponse> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(qrCodeMapper.toResponse(qrCodeService.deactivate(id)));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Ativar QR Code")
    public ResponseEntity<QrCodeResponse> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(qrCodeMapper.toResponse(qrCodeService.activate(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar QR Code")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        qrCodeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UUID currentUserId() {
        return UUID.fromString(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    private ScanEventResponse toScanResponse(ScanEvent e) {
        return new ScanEventResponse(
                e.getId(), e.getShortCode() != null ? e.getShortCode().value() : null,
                e.getIpAddress(), e.getUserAgent(), e.getReferer(), e.getScannedAt());
    }
}
