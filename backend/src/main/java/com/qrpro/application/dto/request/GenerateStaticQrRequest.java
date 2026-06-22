package com.qrpro.application.dto.request;
import com.qrpro.domain.vo.QrCodeDesign;
import jakarta.validation.constraints.NotBlank;
public record GenerateStaticQrRequest(
    @NotBlank(message = "Content is required")
    String content,
    QrCodeDesign design
) {}
