package com.qrpro.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qrpro.application.port.out.ScanEventIngestionPort;
import com.qrpro.domain.model.ScanEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    @SneakyThrows
    public void ingest(ScanEvent event) {
        String json = objectMapper.writeValueAsString(event);
        Long result = redisTemplate.opsForList().leftPush(SCAN_QUEUE, json);
        log.debug("Scan event ingested. Queue size: {}", result);
    }
}
