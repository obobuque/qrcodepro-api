package com.qrpro.infrastructure.adapter.out.persistence.mapper;

import com.qrpro.domain.model.QrCode;
import com.qrpro.domain.model.QrCodeType;
import com.qrpro.domain.vo.*;
import com.qrpro.infrastructure.adapter.out.persistence.entity.QrCodeEntity;
import org.springframework.stereotype.Component;

@Component
public class QrCodeEntityMapper {

    public QrCodeEntity toEntity(QrCode domain) {
        if (domain == null) return null;
        return QrCodeEntity.builder()
            .id(domain.getId())
            .ownerId(domain.getOwnerId())
            .type(domain.getType() == QrCodeType.STATIC
                ? QrCodeEntity.QrCodeTypeEntity.STATIC
                : QrCodeEntity.QrCodeTypeEntity.DYNAMIC)
            .shortCode(domain.getShortCode() != null ? domain.getShortCode().value() : null)
            .content(domain.getContent() != null ? domain.getContent().value() : null)
            .destinationUrl(domain.getDestination() != null ? domain.getDestination().url() : null)
            .foregroundColor(domain.getDesign() != null ? domain.getDesign().foregroundColor() : "#000000")
            .backgroundColor(domain.getDesign() != null ? domain.getDesign().backgroundColor() : "#FFFFFF")
            .dotStyle(domain.getDesign() != null ? domain.getDesign().dotStyle().name() : "SQUARE")
            .errorCorrectionLevel(domain.getDesign() != null && domain.getDesign().errorCorrectionLevel() != null
                ? String.valueOf(domain.getDesign().errorCorrectionLevel().name().charAt(0))
                : "M")
            .imageUrl(domain.getImageUrl())
            .imageFormat("PNG")
            .sizePixels(300)
            .active(domain.isActive())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .expiresAt(domain.getExpiresAt())
            .scanCount(domain.getScanCount())
            .lastScanAt(domain.getLastScanAt())
            .build();
    }

    public QrCode toDomain(QrCodeEntity entity) {
        if (entity == null) return null;

        QrCodeDesign design = new QrCodeDesign(
            entity.getForegroundColor(),
            entity.getBackgroundColor(),
            DotStyle.valueOf(entity.getDotStyle()),
            null,
            ErrorCorrectionLevel.valueOf(
                "L".equals(entity.getErrorCorrectionLevel()) ? "L" :
                "Q".equals(entity.getErrorCorrectionLevel()) ? "Q" :
                "H".equals(entity.getErrorCorrectionLevel()) ? "H" : "M"
            )
        );

        QrCodeType type = entity.getType() == QrCodeEntity.QrCodeTypeEntity.STATIC
                ? QrCodeType.STATIC : QrCodeType.DYNAMIC;

        QrCodeContent content = entity.getContent() != null
                ? new QrCodeContent(entity.getContent()) : null;

        ShortCode shortCode = entity.getShortCode() != null
                ? new ShortCode(entity.getShortCode()) : null;

        DynamicDestination destination = entity.getDestinationUrl() != null
                ? new DynamicDestination(entity.getDestinationUrl()) : null;

        return QrCode.reconstruct(
            entity.getId(),
            entity.getOwnerId(),
            type,
            content,
            shortCode,
            design,
            destination,
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getExpiresAt(),
            entity.getActive() != null && entity.getActive(),
            entity.getScanCount() != null ? entity.getScanCount() : 0L,
            entity.getLastScanAt(),
            entity.getImageUrl()
        );
    }
}
