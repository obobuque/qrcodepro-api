package com.qrpro.infrastructure.adapter.in.web;

import com.qrpro.application.dto.request.GenerateStaticQrRequest;
import com.qrpro.application.dto.response.QrCodeResponse;
import com.qrpro.application.mapper.QrCodeMapper;
import com.qrpro.application.port.in.GenerateQrCodeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/qr")
@RequiredArgsConstructor
@Tag(name = "Public QR Codes", description = "Endpoints publicos para teste (sem autenticacao)")
public class PublicQrController {

    private final GenerateQrCodeUseCase generateQrCodeUseCase;
    private final QrCodeMapper qrCodeMapper;

    @PostMapping("/static")
    @Operation(summary = "Criar QR Code estatico (PUBLICO)", description = "Gera QR sem autenticacao - APENAS PARA TESTE")
    public ResponseEntity<QrCodeResponse> generateStaticPublic(
            @Valid @RequestBody GenerateStaticQrRequest request) {
        
        var command = new GenerateQrCodeUseCase.StaticQrCommand(
            request.ownerId(),
            request.content(),
            request.design()
        );
        
        var qrCode = generateQrCodeUseCase.generateStatic(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(qrCodeMapper.toResponse(qrCode));
    }
}
