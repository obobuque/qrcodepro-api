package com.qrpro.application.dto.response;

public record QrCodeImageResponse(
    byte[] imageData,
    String contentType,
    String fileName
) {}
