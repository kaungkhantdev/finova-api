package com.financial.api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import static com.financial.api.config.SecurityConstants.ACCESS_TOKEN;
import static com.financial.api.config.SecurityConstants.REFRESH_TOKEN;

@Slf4j
@Component
public class CookieUtil {

    @Value("${app.cookie.secure:true}")
    private boolean secure;

    @Value("${app.cookie.max-age:604800}") // 7 days default
    private long maxAge;

    @Value("${app.cookie.domain:}")
    private String domain;

    public String createAccessTokenCookie(String token) {
        return createCookie(ACCESS_TOKEN, token, maxAge);
    }

    public String createRefreshTokenCookie(String token) {
        return createCookie(REFRESH_TOKEN, token, maxAge * 2); // Refresh token lives longer
    }

    public String deleteAccessTokenCookie() {
        return createCookie(ACCESS_TOKEN, "", 0);
    }

    public String deleteRefreshTokenCookie() {
        return createCookie(REFRESH_TOKEN, "", 0);
    }

    private String createCookie(String name, String value, long maxAge) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict");

        if (domain != null && !domain.isEmpty()) {
            builder.domain(domain);
        }

        return builder.build().toString();
    }
}