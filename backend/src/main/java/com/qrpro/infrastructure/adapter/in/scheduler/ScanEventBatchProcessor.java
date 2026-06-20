package com.qrpro.infrastructure.adapter.in.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qrpro.domain.model.ScanEvent;
import com.qrpro.domain.repository.ScanEventRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScanEventBatchProcessor {

    private final StringRedisTemplate redisTemplate;
    private final ScanEventRepositoryPort scanEventRepository;
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

        var events = batch.stream()
            .map(this::parseEvent)
            .toList();

        scanEventRepository.saveAll(events);
        log.info("Saved {} scan events to database", events.size());
    }

    @SneakyThrows
    private ScanEvent parseEvent(String json) {
        return objectMapper.readValue(json, ScanEvent.class);
    }
}
