package com.qrpro.infrastructure.adapter.in.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qrpro.domain.model.ScanEvent;
import com.qrpro.domain.repository.QrCodeRepositoryPort;
import com.qrpro.domain.repository.ScanEventRepositoryPort;
import com.qrpro.domain.vo.ShortCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        List<String> batch = redisTemplate.opsForList()
            .rightPop(SCAN_QUEUE, BATCH_SIZE);

        if (batch == null || batch.isEmpty()) {
            return;
        }

        log.info("Processing batch of {} scan events", batch.size());

        List<ScanEvent> events = batch.stream()
            .map(this::parseEvent)
            .toList();

        // Salva os scan events
        scanEventRepository.saveAll(events);

        // Agrupa por shortCode e incrementa scanCount
        Map<String, Long> countByShortCode = events.stream()
            .filter(e -> e.getShortCode() != null)
            .collect(Collectors.groupingBy(
                e -> e.getShortCode().value(),
                Collectors.counting()
            ));

        countByShortCode.forEach((code, count) -> {
            try {
                qrCodeRepository.findByShortCode(new ShortCode(code)).ifPresent(qrCode -> {
                    for (int i = 0; i < count; i++) {
                        qrCode.recordScan();
                    }
                    qrCodeRepository.save(qrCode);
                    log.debug("Updated scanCount for {}: +{}", code, count);
                });
            } catch (Exception e) {
                log.error("Failed to update scan count for shortCode {}: {}", code, e.getMessage());
            }
        });

        log.info("Processed {} scan events, updated {} QR codes", events.size(), countByShortCode.size());
    }

    @SneakyThrows
    private ScanEvent parseEvent(String json) {
        return objectMapper.readValue(json, ScanEvent.class);
    }
}
