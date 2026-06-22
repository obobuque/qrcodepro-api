package com.qrpro.application.dto.request;
import com.qrpro.domain.vo.QrCodeDesign;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record GenerateDynamicQrRequest(
    @NotBlank(message = "Initial URL is required")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    String initialUrl,
    QrCodeDesign design
) {}
