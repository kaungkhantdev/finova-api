package com.financial.api.config;

public class SecurityConstants {
    private SecurityConstants() {} // prevent instantiation

    public static final String[] PUBLIC_URLS = {
            "/api/v1/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/health"
    };

    public static final String ADMIN_URL = "/api/v1/admin/**";
    public static final String[] USER_URLS = {"/api/v1/users/**"};

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
}
