package com.qrpro.infrastructure.adapter.out.storage;

import com.qrpro.application.port.out.QrCodeStoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@Profile({"dev", "test"})
public class LocalStorageAdapter implements QrCodeStoragePort {

    private static final String STORAGE_DIR = "./qr-storage";

    @Override
    public String store(byte[] imageData, String fileName) {
        try {
            Path dirPath = Paths.get(STORAGE_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            Path filePath = dirPath.resolve(fileName);
            Files.write(filePath, imageData);

            String url = "file://" + filePath.toAbsolutePath();
            log.debug("Stored QR code image: {}", url);
            return url;

        } catch (IOException e) {
            log.error("Failed to store QR code image", e);
            throw new RuntimeException("Failed to store image", e);
        }
    }

    @Override
    public InputStream retrieve(String fileName) {
        try {
            Path filePath = Paths.get(STORAGE_DIR).resolve(fileName);
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            log.error("Failed to retrieve QR code image", e);
            throw new RuntimeException("Failed to retrieve image", e);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            Path filePath = Paths.get(STORAGE_DIR).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete QR code image", e);
        }
    }

    @Override
    public boolean exists(String fileName) {
        Path filePath = Paths.get(STORAGE_DIR).resolve(fileName);
        return Files.exists(filePath);
    }
}
