package com.qrpro.infrastructure.adapter.out.cache;

import com.qrpro.application.port.out.QrCodeCachePort;
import com.qrpro.domain.vo.ShortCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheAdapter implements QrCodeCachePort {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "qr:destination:";
    private static final Duration TTL = Duration.ofHours(24);

    @Override
    public void cacheDestination(ShortCode shortCode, String url) {
        String key = PREFIX + shortCode.value();
        redisTemplate.opsForValue().set(key, url, TTL);
        log.debug("Cached destination for {}: {}", shortCode, url);
    }

    @Override
    public String getDestination(ShortCode shortCode) {
        String key = PREFIX + shortCode.value();
        String url = redisTemplate.opsForValue().get(key);
        if (url != null) {
            log.debug("Cache hit for {}", shortCode);
        }
        return url;
    }

    @Override
    public void invalidate(ShortCode shortCode) {
        String key = PREFIX + shortCode.value();
        redisTemplate.delete(key);
        log.debug("Invalidated cache for {}", shortCode);
    }
}
