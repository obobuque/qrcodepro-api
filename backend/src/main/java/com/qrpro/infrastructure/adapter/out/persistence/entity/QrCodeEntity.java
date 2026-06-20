package com.qrpro.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "qr_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrCodeEntity {

    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private QrCodeTypeEntity type;

    @Column(name = "short_code", unique = true, length = 7)
    private String shortCode;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "destination_url", columnDefinition = "TEXT")
    private String destinationUrl;

    @Column(name = "foreground_color", length = 7)
    private String foregroundColor;

    @Column(name = "background_color", length = 7)
    private String backgroundColor;

    @Column(name = "dot_style", length = 20)
    private String dotStyle;

    @Column(name = "error_correction_level", length = 1)
    private String errorCorrectionLevel;

    @Column(name = "logo_image_url")
    private String logoImageUrl;

    @Column(name = "logo_size_percent")
    private Integer logoSizePercent;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_format", length = 10)
    private String imageFormat;

    @Column(name = "size_pixels")
    private Integer sizePixels;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "scan_count")
    private Long scanCount;

    @Column(name = "last_scan_at")
    private Instant lastScanAt;

    public enum QrCodeTypeEntity {
        STATIC, DYNAMIC
    }
}
