package com.financial.api.controller.api.v1;

import com.financial.api.dto.request.*;
import com.financial.api.dto.response.AuthResponse;
import com.financial.api.service.AuthService;
import com.financial.api.util.AppApiResponse;
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
import static com.financial.api.constant.SecurityConstants.REFRESH_TOKEN;

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
    public ResponseEntity<AppApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);

        String accessTokenCookie = cookieUtil.createAccessTokenCookie(authResponse.getAccessToken());
        String refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authResponse.getRefreshToken());

        AppApiResponse<String> response = AppApiResponse.success("Register successful", null);

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
    public ResponseEntity<AppApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        String accessTokenCookie = cookieUtil.createAccessTokenCookie(authResponse.getAccessToken());
        String refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authResponse.getRefreshToken());

        AppApiResponse<String> response = AppApiResponse.success("Login successful", null);

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
    public ResponseEntity<AppApiResponse<String>> refreshToken(
            @CookieValue(name = REFRESH_TOKEN, required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new Error("Refresh token not found in cookie");
        }

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);
        AuthResponse authResponse = authService.refreshToken(request);

        String accessTokenCookie = cookieUtil.createAccessTokenCookie(authResponse.getAccessToken());
        AppApiResponse<String> response = AppApiResponse.success("Token refreshed successfully", null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie)
                .body(response);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout user (Web)",
            description = "Clear authentication cookies."
    )
    public ResponseEntity<AppApiResponse<String>> logout() {
        String deleteAccessToken = cookieUtil.deleteAccessTokenCookie();
        String deleteRefreshToken = cookieUtil.deleteRefreshTokenCookie();

        AppApiResponse<String> response = AppApiResponse.success("Logout successful", null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccessToken)
                .header(HttpHeaders.SET_COOKIE, deleteRefreshToken)
                .body(response);
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Forgot Password (Web)",
            description = "Sends OTP to the user’s email."
    )
    public ResponseEntity<AppApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        AppApiResponse<String> response = AppApiResponse.success("Otp sent successfully", null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    @Operation(
            summary = "Verify OTP for Password Reset",
            description = "Verify OTP sent to the user’s email before resetting password."
    )
    public ResponseEntity<AppApiResponse<String>> verifyOtp(@RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
        AppApiResponse<String> response = AppApiResponse.success("OTP verified successfully", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset Password (Web)",
            description = "Reset the password using OTP and new password."
    )
    public ResponseEntity<AppApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        AppApiResponse<String> response = AppApiResponse.success("Password reset successfully", null);
        return ResponseEntity.ok(response);
    }


    // ==================== MOBILE ENDPOINTS (Bearer Token) ====================

    @PostMapping("/mobile/register")
    @Operation(
            summary = "Register a new user (Mobile)",
            description = "Create a new user account. Returns JWT tokens in response body."
    )
    @SecurityRequirement(name = "") // No authentication required
    public ResponseEntity<AppApiResponse<AuthResponse>> mobileRegister(@Valid @RequestBody RegisterRequest request) {
        AuthResponse registered = authService.register(request);
        AppApiResponse<AuthResponse> response = AppApiResponse.success(
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
    public ResponseEntity<AppApiResponse<AuthResponse>> mobileLogin(@Valid @RequestBody LoginRequest request) {
        AuthResponse login = authService.login(request);
        AppApiResponse<AuthResponse> response = AppApiResponse.success(
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
    public ResponseEntity<AppApiResponse<AuthResponse>> mobileRefreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse refreshToken = authService.refreshToken(request);
        AppApiResponse<AuthResponse> response = AppApiResponse.success(
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
    public ResponseEntity<AppApiResponse<String>> mobileLogout() {
        // In stateless JWT, logout is handled client-side by removing the token
        AppApiResponse<String> response = AppApiResponse.success(
                "Logout successfully",
                null
        );
        return ResponseEntity.ok(response);
    }
}