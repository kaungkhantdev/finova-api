package com.financial.api.service.impl;

import com.financial.api.service.RateLimitService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.financial.api.config.RateLimitConfig.PUBLIC_REQUESTS_PER_MINUTE;
import static com.financial.api.config.RateLimitConfig.REQUESTS_PER_MINUTE;

@Service
public class RateLimitServiceImpl implements RateLimitService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket(REQUESTS_PER_MINUTE));
    }

    public Bucket resolvePublicBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket(PUBLIC_REQUESTS_PER_MINUTE));
    }

    private Bucket createNewBucket(int requestsPerMinute) {
        Bandwidth limit = Bandwidth.classic(
                requestsPerMinute,
                Refill.intervally(requestsPerMinute, Duration.ofMinutes(1))
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public void resetBucket(String key) {
        cache.remove(key);
    }
}
