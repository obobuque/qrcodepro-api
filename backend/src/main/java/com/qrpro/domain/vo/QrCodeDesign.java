package com.qrpro.domain.vo;

public record QrCodeDesign(
    String foregroundColor,
    String backgroundColor,
    DotStyle dotStyle,
    LogoConfig logo,
    ErrorCorrectionLevel errorCorrectionLevel
) {
    public QrCodeDesign {
        if (foregroundColor == null || !foregroundColor.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException("Invalid foreground color. Use hex format #RRGGBB");
        }
        if (backgroundColor == null || !backgroundColor.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException("Invalid background color. Use hex format #RRGGBB");
        }
        if (dotStyle == null) {
            dotStyle = DotStyle.SQUARE;
        }
        if (errorCorrectionLevel == null) {
            errorCorrectionLevel = ErrorCorrectionLevel.M;
        }
        if (logo != null && errorCorrectionLevel.ordinal() < ErrorCorrectionLevel.H.ordinal()) {
            throw new IllegalArgumentException(
                "Logo requires high error correction level (H). Current: " + errorCorrectionLevel
            );
        }
    }

    public static QrCodeDesign defaultDesign() {
        return new QrCodeDesign("#000000", "#FFFFFF", DotStyle.SQUARE, null, ErrorCorrectionLevel.M);
    }
}
