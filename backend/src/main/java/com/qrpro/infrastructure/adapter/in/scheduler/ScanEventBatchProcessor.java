package com.qrpro.infrastructure.adapter.in.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qrpro.domain.model.ScanEvent;
import com.qrpro.domain.repository.QrCodeRepositoryPort;
import com.qrpro.domain.repository.ScanEventRepositoryPort;
import com.qrpro.domain.vo.ShortCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScanEventBatchProcessor {

    private final StringRedisTemplate redisTemplate;
    private final ScanEventRepositoryPort scanEventRepository;
    private final QrCodeRepositoryPort qrCodeRepository;
    private final ObjectMapper objectMapper;

    private static final String SCAN_QUEUE = "qr:scan:events";
    private static final int BATCH_SIZE = 500;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    @Transactional
    public void processBatch() {
        List<String> batch = redisTemplate.opsForList().rightPop(SCAN_QUEUE, BATCH_SIZE);
        if (batch == null || batch.isEmpty()) return;

        log.info("Processing batch of {} scan events", batch.size());

        List<ScanEvent> events = new ArrayList<>();
        Map<String, Long> countByShortCode = new HashMap<>();

        for (String json : batch) {
            try {
                JsonNode node = objectMapper.readTree(json);
                String shortCodeStr = node.has("shortCode") && !node.get("shortCode").isNull()
                    ? node.get("shortCode").asText() : null;
                String qrCodeIdStr = node.has("qrCodeId") && !node.get("qrCodeId").isNull()
                    ? node.get("qrCodeId").asText() : null;

                UUID qrCodeId = qrCodeIdStr != null ? UUID.fromString(qrCodeIdStr) : null;
                ShortCode shortCode = shortCodeStr != null ? new ShortCode(shortCodeStr) : null;

                var event = ScanEvent.create(
                    qrCodeId,
                    shortCode,
                    node.has("ipAddress") ? node.get("ipAddress").asText(null) : null,
                    node.has("userAgent") ? node.get("userAgent").asText(null) : null,
                    node.has("referer") ? node.get("referer").asText(null) : null,
                    node.has("scannedAt") ? Instant.parse(node.get("scannedAt").asText()) : Instant.now()
                );
                events.add(event);

                if (shortCodeStr != null) {
                    countByShortCode.merge(shortCodeStr, 1L, Long::sum);
                }
            } catch (Exception e) {
                log.error("Failed to parse scan event: {}", e.getMessage());
            }
        }

        if (!events.isEmpty()) {
            scanEventRepository.saveAll(events);
        }

        countByShortCode.forEach((code, count) -> {
            try {
                qrCodeRepository.findByShortCode(new ShortCode(code)).ifPresent(qrCode -> {
                    for (int i = 0; i < count; i++) qrCode.recordScan();
                    qrCodeRepository.save(qrCode);
                    log.info("Updated scanCount for {}: +{}", code, count);
                });
            } catch (Exception e) {
                log.error("Failed to update scan count for {}: {}", code, e.getMessage());
            }
        });

        log.info("Processed {} events, updated {} QR codes", events.size(), countByShortCode.size());
    }
}
