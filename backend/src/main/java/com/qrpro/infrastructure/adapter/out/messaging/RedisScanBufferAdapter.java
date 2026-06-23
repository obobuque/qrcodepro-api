package com.qrpro.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qrpro.application.port.out.ScanEventIngestionPort;
import com.qrpro.domain.model.ScanEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisScanBufferAdapter implements ScanEventIngestionPort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String SCAN_QUEUE = "qr:scan:events";

    @Override
    public void ingest(ScanEvent event) {
        try {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("id", event.getId().toString());
            node.put("qrCodeId", event.getQrCodeId() != null ? event.getQrCodeId().toString() : null);
            node.put("shortCode", event.getShortCode() != null ? event.getShortCode().value() : null);
            node.put("ipAddress", event.getIpAddress());
            node.put("userAgent", event.getUserAgent());
            node.put("referer", event.getReferer());
            node.put("scannedAt", event.getScannedAt().toString());
            String json = objectMapper.writeValueAsString(node);
            Long size = redisTemplate.opsForList().leftPush(SCAN_QUEUE, json);
            log.debug("Scan event ingested. Queue size: {}", size);
        } catch (Exception e) {
            log.error("Failed to ingest scan event: {}", e.getMessage());
        }
    }
}
