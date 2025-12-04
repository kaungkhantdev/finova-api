package com.finova.api.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    // Rate limit: 100 requests per minute per user
    public static final int REQUESTS_PER_MINUTE = 100;

    // Rate limit for public endpoints: 20 requests per minute per IP
    public static final int PUBLIC_REQUESTS_PER_MINUTE = 20;

    // Burst capacity
    public static final int BURST_CAPACITY = 10;
}