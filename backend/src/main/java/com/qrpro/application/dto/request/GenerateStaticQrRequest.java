package com.qrpro.application.dto.request;

import com.qrpro.domain.vo.QrCodeDesign;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenerateStaticQrRequest(
    @NotBlank(message = "Content is required")
    String content,

    QrCodeDesign design,

    @NotNull(message = "Owner ID is required")
    String ownerId
) {}
