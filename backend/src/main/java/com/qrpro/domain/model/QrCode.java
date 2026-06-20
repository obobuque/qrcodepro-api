package com.qrpro.domain.model;

import com.qrpro.domain.vo.*;
import java.time.Instant;
import java.util.UUID;

public class QrCode {
    private final UUID id;
    private final UUID ownerId;
    private final QrCodeType type;
    private final QrCodeContent content;
    private final ShortCode shortCode;
    private QrCodeDesign design;
    private DynamicDestination destination;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiresAt;
    private boolean active;
    private long scanCount;
    private Instant lastScanAt;
    private String imageUrl;

    private QrCode(UUID id, UUID ownerId, QrCodeType type, QrCodeContent content,
                   ShortCode shortCode, QrCodeDesign design, DynamicDestination destination,
                   Instant createdAt, Instant expiresAt, boolean active) {
        this.id = id;
        this.ownerId = ownerId;
        this.type = type;
        this.content = content;
        this.shortCode = shortCode;
        this.design = design;
        this.destination = destination;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.expiresAt = expiresAt;
        this.active = active;
        this.scanCount = 0;
    }


    public static QrCode reconstruct(UUID id, UUID ownerId, QrCodeType type,
                                     QrCodeContent content, ShortCode shortCode,
                                     QrCodeDesign design, DynamicDestination destination,
                                     Instant createdAt, Instant updatedAt, Instant expiresAt,
                                     boolean active, long scanCount, Instant lastScanAt,
                                     String imageUrl) {
        QrCode qr = new QrCode(id, ownerId, type, content, shortCode, design, destination,
                createdAt, expiresAt, active);
        qr.updatedAt = updatedAt;
        qr.scanCount = scanCount;
        qr.lastScanAt = lastScanAt;
        qr.imageUrl = imageUrl;
        return qr;
    }

    public static QrCode createStatic(UUID ownerId, QrCodeContent content, QrCodeDesign design) {
        if (content == null || content.value().isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty for static QR code");
        }
        return new QrCode(
            UUID.randomUUID(), ownerId, QrCodeType.STATIC,
            content, null, design, null,
            Instant.now(), null, true
        );
    }

    public static QrCode createDynamic(UUID ownerId, ShortCode shortCode,
                                       DynamicDestination destination, QrCodeDesign design) {
        if (shortCode == null) {
            throw new IllegalArgumentException("Short code is required for dynamic QR code");
        }
        if (destination == null || destination.url().isBlank()) {
            throw new IllegalArgumentException("Destination URL cannot be empty");
        }
        return new QrCode(
            UUID.randomUUID(), ownerId, QrCodeType.DYNAMIC,
            null, shortCode, design, destination,
            Instant.now(), null, true
        );
    }

    public void updateDestination(DynamicDestination newDestination) {
        if (type != QrCodeType.DYNAMIC) {
            throw new IllegalStateException("Only dynamic QR codes can change destination");
        }
        if (newDestination == null || newDestination.url().isBlank()) {
            throw new IllegalArgumentException("Destination URL cannot be empty");
        }
        this.destination = newDestination;
        this.updatedAt = Instant.now();
    }

    public void updateDesign(QrCodeDesign newDesign) {
        this.design = newDesign;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void recordScan() {
        this.scanCount++;
        this.lastScanAt = Instant.now();
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public String resolveTargetUrl() {
        if (type == QrCodeType.STATIC) {
            return content != null ? content.value() : null;
        }
        return destination != null ? destination.url() : null;
    }

    public String resolveContentForGeneration() {
        if (type == QrCodeType.STATIC) {
            return content.value();
        }
        return shortCode != null ? shortCode.toRedirectUrl() : null;
    }

    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public QrCodeType getType() { return type; }
    public QrCodeContent getContent() { return content; }
    public ShortCode getShortCode() { return shortCode; }
    public QrCodeDesign getDesign() { return design; }
    public DynamicDestination getDestination() { return destination; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isActive() { return active; }
    public long getScanCount() { return scanCount; }
    public Instant getLastScanAt() { return lastScanAt; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
