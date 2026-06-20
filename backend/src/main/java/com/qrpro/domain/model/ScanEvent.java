package com.qrpro.domain.model;

import com.qrpro.domain.vo.ShortCode;
import java.time.Instant;
import java.util.UUID;

public class ScanEvent {
    private final UUID id;
    private final UUID qrCodeId;
    private final ShortCode shortCode;
    private final String ipAddress;
    private final String userAgent;
    private final String referer;
    private final String countryCode;
    private final String deviceType;
    private final String os;
    private final String browser;
    private final Instant scannedAt;
    private final Double latitude;
    private final Double longitude;

    private ScanEvent(UUID id, UUID qrCodeId, ShortCode shortCode,
                      String ipAddress, String userAgent, String referer,
                      String countryCode, String deviceType, String os,
                      String browser, Instant scannedAt,
                      Double latitude, Double longitude) {
        this.id = id;
        this.qrCodeId = qrCodeId;
        this.shortCode = shortCode;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.referer = referer;
        this.countryCode = countryCode;
        this.deviceType = deviceType;
        this.os = os;
        this.browser = browser;
        this.scannedAt = scannedAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static ScanEvent create(UUID qrCodeId, ShortCode shortCode,
                                   String ipAddress, String userAgent,
                                   String referer, Instant scannedAt) {
        return new ScanEvent(
            UUID.randomUUID(), qrCodeId, shortCode,
            ipAddress, userAgent, referer,
            null, null, null, null, scannedAt,
            null, null
        );
    }

    public UUID getId() { return id; }
    public UUID getQrCodeId() { return qrCodeId; }
    public ShortCode getShortCode() { return shortCode; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getReferer() { return referer; }
    public String getCountryCode() { return countryCode; }
    public String getDeviceType() { return deviceType; }
    public String getOs() { return os; }
    public String getBrowser() { return browser; }
    public Instant getScannedAt() { return scannedAt; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
}
