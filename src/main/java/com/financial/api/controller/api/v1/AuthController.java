package com.financial.api.controller.api.v1;

import com.financial.api.dto.request.LoginRequest;
import com.financial.api.dto.request.RegisterRequest;
import com.financial.api.dto.request.RefreshTokenRequest;
import com.financial.api.dto.response.AuthResponse;
import com.financial.api.dto.response.CurrencyResponse;
import com.financial.api.service.AuthService;
import com.financial.api.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse registered = authService.register(request);
        ApiResponse<AuthResponse> response = ApiResponse.success(
                "Register successfully",
                registered
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse login = authService.login(request);
        ApiResponse<AuthResponse> response = ApiResponse.success(
                "Login successfully",
                login
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse refreshToken = authService.refreshToken(request);
        ApiResponse<AuthResponse> response = ApiResponse.success(
                "Login successfully",
                refreshToken
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<AuthResponse>> logout() {
        // In stateless JWT, logout is handled client-side by removing the token
        ApiResponse<AuthResponse> response = ApiResponse.success(
                "Logout successfully",
                null
        );
        return ResponseEntity.ok(response);
    }
}