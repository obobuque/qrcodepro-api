package com.qrpro.application.dto.response;

public record ScanResultResponse(
    String redirectUrl,
    boolean tracked,
    String message
) {}
