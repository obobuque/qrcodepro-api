package com.qrpro.application.port.out;

public interface NotificationPort {
    void sendQrCodeGenerated(String email, String qrCodeId, String imageUrl);
    void sendScanAlert(String email, String qrCodeId, long scanCount);
}
