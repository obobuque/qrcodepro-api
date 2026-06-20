package com.qrpro.infrastructure.adapter.out.storage;

import com.qrpro.application.port.out.QrCodeStoragePort;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class S3StorageAdapter implements QrCodeStoragePort {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.public-url}")
    private String publicUrl;

    @Override
    public String store(byte[] imageData, String fileName) {
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .stream(new ByteArrayInputStream(imageData), imageData.length, -1)
                    .contentType("image/png")
                    .build()
            );
            String url = publicUrl + "/" + fileName;
            log.debug("Stored QR code image in R2: {}", url);
            return url;
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to store QR code in R2", e);
            throw new RuntimeException("Failed to store image in R2", e);
        }
    }

    @Override
    public InputStream retrieve(String fileName) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(String fileName) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean exists(String fileName) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
