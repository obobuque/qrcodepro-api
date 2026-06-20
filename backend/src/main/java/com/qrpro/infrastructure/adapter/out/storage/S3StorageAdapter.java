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

            String url = minioClient.getPresignedObjectUrl(
                io.minio.GetPresignedObjectUrlArgs.builder()
                    .method(io.minio.http.Method.GET)
                    .bucket(bucket)
                    .object(fileName)
                    .build()
            );

            log.debug("Stored QR code image in S3: {}", fileName);
            return url;

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to store QR code in S3", e);
            throw new RuntimeException("Failed to store image in S3", e);
        }
    }

    @Override
    public InputStream retrieve(String fileName) {
        // Implementar se necessário
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(String fileName) {
        // Implementar se necessário
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean exists(String fileName) {
        // Implementar se necessário
        throw new UnsupportedOperationException("Not implemented");
    }
}
