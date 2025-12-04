package com.finova.api.controller.api.v1;

import com.finova.api.dto.request.*;
import com.finova.api.dto.request.*;
import com.finova.api.dto.response.AuthResponse;
import com.finova.api.service.AuthService;
import com.finova.api.util.AppApiResponse;
import com.finova.api.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

import static com.finova.api.config.OpenApiConfig.BEARER_AUTH;
import static com.finova.api.constant.SecurityConstants.REFRESH_TOKEN;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints for web and mobile clients")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    // ==================== WEB ENDPOINTS (Cookie-based) ====================

    @PostMapping("/register")
    @Operation(summary = "Register a new user (Web)",
            description = "Create a new user account. Returns JWT tokens in HTTP-only cookies.")
    @SecurityRequirement(name = "")
    public ResponseEntity<AppApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        return buildCookieResponse(authResponse, "Register successful");
    }

    @PostMapping("/login")
    @Operation(summary = "Login user (Web)",
            description = "Authenticate user and return JWT tokens in HTTP-only cookies.")
    @SecurityRequirement(name = "")
    public ResponseEntity<AppApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return buildCookieResponse(authResponse, "Login successful");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token (Web)",
            description = "Get a new access token using the refresh token from cookie.")
    public ResponseEntity<AppApiResponse<String>> refreshToken(
            @CookieValue(name = REFRESH_TOKEN, required = false) String refreshToken) {

        validateRefreshToken(refreshToken);

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);
        AuthResponse authResponse = authService.refreshToken(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(authResponse.getAccessToken()))
                .body(AppApiResponse.success("Token refreshed successfully", null));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user (Web)",
            description = "Clear authentication cookies.")
    public ResponseEntity<AppApiResponse<String>> logout() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.deleteAccessTokenCookie())
                .header(HttpHeaders.SET_COOKIE, cookieUtil.deleteRefreshTokenCookie())
                .body(AppApiResponse.success("Logout successful", null));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password (Web)",
            description = "Sends OTP to the user's email.")
    public ResponseEntity<AppApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.forgotPassword(request);
            return ResponseEntity.ok(AppApiResponse.success("OTP sent successfully", null));
        } catch (NoSuchElementException ex) {
            // Handle specific exception for user not found
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(AppApiResponse.error(ex.getMessage(), null));
        } catch (Exception ex) {
            // Log the full error for debugging
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AppApiResponse.error(ex.getMessage(), null));
        }
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP for Password Reset",
            description = "Verify OTP sent to the user's email before resetting password.")
    public ResponseEntity<AppApiResponse<Map<String, Object>>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        Map<String, Object> token = authService.verifyOtp(request);
        return ResponseEntity.ok(AppApiResponse.success("OTP verified successfully", token));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password (Web)",
            description = "Reset the password using OTP and new password.")
    public ResponseEntity<AppApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(AppApiResponse.success("Password reset successfully", null));
    }

    // ==================== MOBILE ENDPOINTS (Bearer Token) ====================

    @PostMapping("/mobile/register")
    @Operation(summary = "Register a new user (Mobile)",
            description = "Create a new user account. Returns JWT tokens in response body.")
    @SecurityRequirement(name = "")
    public ResponseEntity<AppApiResponse<AuthResponse>> mobileRegister(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        return buildMobileResponse(authResponse, "Register successful");
    }

    @PostMapping("/mobile/login")
    @Operation(summary = "Login user (Mobile)",
            description = "Authenticate user and return JWT tokens in response body.")
    @SecurityRequirement(name = "")
    public ResponseEntity<AppApiResponse<AuthResponse>> mobileLogin(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return buildMobileResponse(authResponse, "Login successful");
    }

    @PostMapping("/mobile/refresh")
    @Operation(summary = "Refresh access token (Mobile)",
            description = "Get a new access token using the refresh token from request body.")
    @SecurityRequirement(name = BEARER_AUTH)
    public ResponseEntity<AppApiResponse<AuthResponse>> mobileRefreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refreshToken(request);
        return buildMobileResponse(authResponse, "Token refreshed successfully");
    }

    @PostMapping("/mobile/logout")
    @Operation(summary = "Logout user (Mobile)",
            description = "In stateless JWT, logout is handled client-side by removing the token.")
    @SecurityRequirement(name = BEARER_AUTH)
    public ResponseEntity<AppApiResponse<String>> mobileLogout() {
        return ResponseEntity.ok(AppApiResponse.success("Logout successful", null));
    }

    @PostMapping("/mobile/forgot-password")
    @Operation(summary = "Forgot Password (Mobile)",
            description = "Sends OTP to the user's email.")
    public ResponseEntity<AppApiResponse<String>> mobileForgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(AppApiResponse.success("OTP sent successfully", null));
    }

    @PostMapping("/mobile/verify-otp")
    @Operation(summary = "Verify OTP for Password Reset (Mobile)",
            description = "Verify OTP sent to the user's email before resetting password.")
    public ResponseEntity<AppApiResponse<String>> mobileVerifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(AppApiResponse.success("OTP verified successfully", null));
    }

    @PostMapping("/mobile/reset-password")
    @Operation(summary = "Reset Password (Mobile)",
            description = "Reset the password using OTP and new password.")
    public ResponseEntity<AppApiResponse<String>> mobileResetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(AppApiResponse.success("Password reset successfully", null));
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Build response with authentication cookies (for web endpoints)
     */
    private ResponseEntity<AppApiResponse<String>> buildCookieResponse(AuthResponse authResponse, String message) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(authResponse.getAccessToken()))
                .header(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(authResponse.getRefreshToken()))
                .body(AppApiResponse.success(message, null));
    }

    /**
     * Build response with authentication data in body (for mobile endpoints)
     */
    private ResponseEntity<AppApiResponse<AuthResponse>> buildMobileResponse(AuthResponse authResponse, String message) {
        return ResponseEntity.ok(AppApiResponse.success(message, authResponse));
    }

    /**
     * Validate refresh token from cookie
     */
    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token not found in cookie");
        }
    }
}