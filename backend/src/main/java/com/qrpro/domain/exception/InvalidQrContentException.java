package com.qrpro.domain.exception;

public class InvalidQrContentException extends RuntimeException {
    public InvalidQrContentException(String message) {
        super(message);
    }
}
