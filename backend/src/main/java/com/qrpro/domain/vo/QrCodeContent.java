package com.qrpro.domain.vo;

public record QrCodeContent(String value) {
    public QrCodeContent {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("QR code content cannot be empty");
        }
        if (value.length() > 7089) {
            throw new IllegalArgumentException("QR code content exceeds maximum length of 7089 characters");
        }
    }

    public int length() {
        return value.length();
    }
}
