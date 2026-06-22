package com.qrpro.infrastructure.adapter.out.storage;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.qrpro.application.port.out.QrCodeStoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Component
@Profile("prod")
public class S3StorageAdapter implements QrCodeStoragePort {

    private final AmazonS3 s3Client;
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
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(endpoint, "auto"))
                .withCredentials(new AWSStaticCredentialsProvider(
                    new BasicAWSCredentials(accessKeyId, secretAccessKey)))
                .withPathStyleAccessEnabled(true)
                .build();
        log.info("R2 storage inicializado (SDK v1): bucket={}, prefix={}", bucket,
                accessKeyId.substring(0, Math.min(4, accessKeyId.length())));
    }

    @Override
    public String store(byte[] imageData, String fileName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/png");
        metadata.setContentLength(imageData.length);
        s3Client.putObject(bucket, fileName, new ByteArrayInputStream(imageData), metadata);
        String url = publicUrl + "/" + fileName;
        log.info("QR salvo no R2: {}", url);
        return url;
    }

    @Override
    public InputStream retrieve(String fileName) {
        return s3Client.getObject(bucket, fileName).getObjectContent();
    }

    @Override
    public void delete(String fileName) {
        s3Client.deleteObject(bucket, fileName);
        log.info("QR deletado do R2: {}", fileName);
    }

    @Override
    public boolean exists(String fileName) {
        return s3Client.doesObjectExist(bucket, fileName);
    }
}
