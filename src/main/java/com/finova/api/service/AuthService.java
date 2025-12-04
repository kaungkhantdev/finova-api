package com.finova.api.service;

import com.finova.api.dto.request.*;
import com.finova.api.dto.request.*;
import com.finova.api.dto.response.AuthResponse;

import java.util.Map;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    Map<String, Object> verifyOtp(VerifyOtpRequest request);
    void resetPassword(ResetPasswordRequest request);

}