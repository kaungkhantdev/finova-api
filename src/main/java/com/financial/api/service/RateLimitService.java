package com.financial.api.service;

import io.github.bucket4j.Bucket;

public interface RateLimitService {
    public Bucket resolveBucket(String key);
    public Bucket resolvePublicBucket(String key);
    public void resetBucket(String key);
}
