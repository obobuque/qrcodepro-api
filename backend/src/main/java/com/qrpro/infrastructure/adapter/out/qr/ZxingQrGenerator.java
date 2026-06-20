package com.qrpro.infrastructure.adapter.out.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.qrpro.domain.model.QrCode;
import com.qrpro.domain.service.QrCodeGenerator;
import com.qrpro.domain.vo.QrCodeDesign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ZxingQrGenerator implements QrCodeGenerator {

    @Override
    public byte[] generate(QrCode qrCode) {
        return generateWithDesign(qrCode, qrCode.getDesign());
    }

    @Override
    public byte[] generateWithDesign(QrCode qrCode, QrCodeDesign design) {
        try {
            String content = qrCode.resolveContentForGeneration();
            if (content == null) {
                throw new IllegalStateException("QR code has no content to encode");
            }

            int size = design != null && design.logo() != null ? 500 : 300;

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2);

            ErrorCorrectionLevel ecLevel = design != null && design.errorCorrectionLevel() != null
                ? design.errorCorrectionLevel().toZxing()
                : ErrorCorrectionLevel.M;

            hints.put(EncodeHintType.ERROR_CORRECTION, ecLevel);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            if (design != null) {
                qrImage = applyColors(qrImage, design);
            }

            if (design != null && design.logo() != null) {
                qrImage = overlayLogo(qrImage, design);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", outputStream);

            return outputStream.toByteArray();

        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code", e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    private BufferedImage applyColors(BufferedImage qrImage, QrCodeDesign design) {
        int width = qrImage.getWidth();
        int height = qrImage.getHeight();
        BufferedImage coloredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Color foreground = Color.decode(design.foregroundColor());
        Color background = Color.decode(design.backgroundColor());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = qrImage.getRGB(x, y);
                if (pixel == Color.BLACK.getRGB()) {
                    coloredImage.setRGB(x, y, foreground.getRGB());
                } else {
                    coloredImage.setRGB(x, y, background.getRGB());
                }
            }
        }

        return coloredImage;
    }

    private BufferedImage overlayLogo(BufferedImage qrImage, QrCodeDesign design) {
        return qrImage;
    }
}
