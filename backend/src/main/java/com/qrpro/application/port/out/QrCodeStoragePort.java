package com.qrpro.application.port.out;

import java.io.InputStream;

public interface QrCodeStoragePort {
    String store(byte[] imageData, String fileName);
    InputStream retrieve(String fileName);
    void delete(String fileName);
    boolean exists(String fileName);
}
