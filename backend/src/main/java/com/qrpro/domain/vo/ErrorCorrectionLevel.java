package com.qrpro.domain.vo;

public enum ErrorCorrectionLevel {
    L(0),
    M(1),
    Q(2),
    H(3);

    private final int zxingLevel;

    ErrorCorrectionLevel(int zxingLevel) {
        this.zxingLevel = zxingLevel;
    }

    public int zxingLevel() {
        return zxingLevel;
    }

    public com.google.zxing.qrcode.decoder.ErrorCorrectionLevel toZxing() {
        return com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.values()[zxingLevel];
    }
}
