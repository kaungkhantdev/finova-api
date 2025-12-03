package com.financial.api.constant;

public class SecurityConstants {
    private SecurityConstants() {} // prevent instantiation

    public static final String[] PUBLIC_URLS = {
            "/api/v1/auth/**",
            "/api/v1/public/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/health",
            "/api-docs/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };

    public static final String ADMIN_URL = "/api/v1/admin/**";
    public static final String[] USER_URLS = {"/api/v1/users/**"};

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
}
