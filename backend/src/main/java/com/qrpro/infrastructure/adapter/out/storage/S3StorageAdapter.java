package com.qrpro.infrastructure.adapter.out.storage;

import com.qrpro.application.port.out.QrCodeStoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import java.io.InputStream;
import java.net.URI;

@Slf4j
@Component
@Profile("prod")
public class S3StorageAdapter implements QrCodeStoragePort {

    private final S3Client s3Client;
    private final String bucket;
    private final String publicUrl;

    public S3StorageAdapter(
            @Value("${r2.access-key-id}") String accessKeyId,
            @Value("${r2.secret-access-key}") String secretAccessKey,
            @Value("${r2.endpoint}") String endpoint,
            @Value("${r2.bucket-name}") String bucket,
            @Value("${r2.public-url}") String publicUrl) {
        this.bucket = bucket;
        this.publicUrl = publicUrl;
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .region(Region.US_EAST_1)
                .forcePathStyle(true)
                .build();
    }

    @Override
    public String store(byte[] imageData, String fileName) {
        s3Client.putObject(
                b -> b.bucket(bucket).key(fileName).contentType("image/png"),
                RequestBody.fromBytes(imageData)
        );
        String url = publicUrl + "/" + fileName;
        log.info("QR salvo no R2: {}", url);
        return url;
    }

    @Override
    public InputStream retrieve(String fileName) {
        return s3Client.getObject(b -> b.bucket(bucket).key(fileName));
    }

    @Override
    public void delete(String fileName) {
        s3Client.deleteObject(b -> b.bucket(bucket).key(fileName));
        log.info("QR deletado do R2: {}", fileName);
    }

    @Override
    public boolean exists(String fileName) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucket).key(fileName).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }
}
