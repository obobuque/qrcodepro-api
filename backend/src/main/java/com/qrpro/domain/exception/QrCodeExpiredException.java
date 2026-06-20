package com.qrpro.domain.exception;

public class QrCodeExpiredException extends RuntimeException {
    public QrCodeExpiredException(String shortCode) {
        super("QR code with short code " + shortCode + " has expired");
    }
}
