package com.qrpro.domain.service;

import com.qrpro.domain.model.QrCode;

public class QrCodeValidator {

    public void validateForGeneration(QrCode qrCode) {
        if (qrCode == null) {
            throw new IllegalArgumentException("QR code cannot be null");
        }
        if (!qrCode.isActive()) {
            throw new IllegalStateException("QR code is inactive");
        }
        if (qrCode.isExpired()) {
            throw new IllegalStateException("QR code has expired");
        }
        String content = qrCode.resolveContentForGeneration();
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("QR code has no content to encode");
        }
    }

    public void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        if (content.length() > 7089) {
            throw new IllegalArgumentException("Content exceeds maximum QR code capacity");
        }
    }
}
