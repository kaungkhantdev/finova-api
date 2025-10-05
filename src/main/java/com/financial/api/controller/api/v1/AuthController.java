package com.financial.api.controller.api.v1;

import com.financial.api.dto.request.LoginRequest;
import com.financial.api.dto.request.RegisterRequest;
import com.financial.api.dto.request.RefreshTokenRequest;
import com.financial.api.dto.response.AuthResponse;
import com.financial.api.service.AuthService;
import com.financial.api.util.ApiResponse;
import com.financial.api.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.financial.api.config.OpenApiConfig.BEARER_AUTH;
import static com.financial.api.config.SecurityConstants.REFRESH_TOKEN;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints for web and mobile clients")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    // ==================== WEB ENDPOINTS (Cookie-based) ====================

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user (Web)",
            description = "Create a new user account. Returns JWT tokens in HTTP-only cookies."
    )
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);

        String accessTokenCookie = cookieUtil.createAccessTokenCookie(authResponse.getAccessToken());
        String refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authResponse.getRefreshToken());

        ApiResponse<String> response = ApiResponse.success("Register successful", null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie)
                .body(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user (Web)",
            description = "Authenticate user and return JWT tokens in HTTP-only cookies."
    )
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        String accessTokenCookie = cookieUtil.createAccessTokenCookie(authResponse.getAccessToken());
        String refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authResponse.getRefreshToken());

        ApiResponse<String> response = ApiResponse.success("Login successful", null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie)
                .body(response);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token (Web)",
            description = "Get a new access token using the refresh token from cookie."
    )
    public ResponseEntity<ApiResponse<String>> refreshToken(
            @CookieValue(name = REFRESH_TOKEN, required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new Error("Refresh token not found in cookie");
        }

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);
        AuthResponse authResponse = authService.refreshToken(request);

        String accessTokenCookie = cookieUtil.createAccessTokenCookie(authResponse.getAccessToken());
        ApiResponse<String> response = ApiResponse.success("Token refreshed successfully", null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie)
                .body(response);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout user (Web)",
            description = "Clear authentication cookies."
    )
    public ResponseEntity<ApiResponse<String>> logout() {
        String deleteAccessToken = cookieUtil.deleteAccessTokenCookie();
        String deleteRefreshToken = cookieUtil.deleteRefreshTokenCookie();

        ApiResponse<String> response = ApiResponse.success("Logout successful", null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccessToken)
                .header(HttpHeaders.SET_COOKIE, deleteRefreshToken)
                .body(response);
    }

    // ==================== MOBILE ENDPOINTS (Bearer Token) ====================

    @PostMapping("/mobile/register")
    @Operation(
            summary = "Register a new user (Mobile)",
            description = "Create a new user account. Returns JWT tokens in response body."
    )
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<ApiResponse<AuthResponse>> mobileRegister(@Valid @RequestBody RegisterRequest request) {
        AuthResponse registered = authService.register(request);
        ApiResponse<AuthResponse> response = ApiResponse.success(
                "Register successfully",
                registered
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mobile/login")
    @Operation(
            summary = "Login user (Mobile)",
            description = "Authenticate user and return JWT tokens in response body."
    )
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<ApiResponse<AuthResponse>> mobileLogin(@Valid @RequestBody LoginRequest request) {
        AuthResponse login = authService.login(request);
        ApiResponse<AuthResponse> response = ApiResponse.success(
                "Login successfully",
                login
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mobile/refresh")
    @Operation(
            summary = "Refresh access token (Mobile)",
            description = "Get a new access token using the refresh token from request body."
    )
    @SecurityRequirement(name = BEARER_AUTH)
    public ResponseEntity<ApiResponse<AuthResponse>> mobileRefreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse refreshToken = authService.refreshToken(request);
        ApiResponse<AuthResponse> response = ApiResponse.success(
                "Token refreshed successfully",
                refreshToken
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mobile/logout")
    @Operation(
            summary = "Logout user (Mobile)",
            description = "In stateless JWT, logout is handled client-side by removing the token."
    )
    @SecurityRequirement(name = BEARER_AUTH)
    public ResponseEntity<ApiResponse<String>> mobileLogout() {
        // In stateless JWT, logout is handled client-side by removing the token
        ApiResponse<String> response = ApiResponse.success(
                "Logout successfully",
                null
        );
        return ResponseEntity.ok(response);
    }
}