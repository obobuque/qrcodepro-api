package com.qrpro.domain.vo;

public record LogoConfig(
    byte[] imageData,
    int sizePercent,
    ErrorCorrectionLevel requiredLevel
) {
    public LogoConfig {
        if (imageData == null || imageData.length == 0) {
            throw new IllegalArgumentException("Logo image data cannot be empty");
        }
        if (sizePercent < 10 || sizePercent > 35) {
            throw new IllegalArgumentException("Logo size must be between 10% and 35%");
        }
        if (requiredLevel == null) {
            requiredLevel = ErrorCorrectionLevel.H;
        }
    }
}
