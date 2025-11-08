package com.financial.api.service;

import com.financial.api.dto.request.*;
import com.financial.api.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    boolean verifyOtp(VerifyOtpRequest request);
    void resetPassword(ResetPasswordRequest request);

}