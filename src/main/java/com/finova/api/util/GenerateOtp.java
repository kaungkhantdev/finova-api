package com.finova.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

import static com.finova.api.constant.OtpConstants.OTP_CHARS;

@Component
public class GenerateOtp {

    @Value("${otp.length:6}")
    private int otpLength;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generate cryptographically secure OTP
     */
    public String generateSecureOtp() {
        StringBuilder otp = new StringBuilder(otpLength);

        for (int i = 0; i < otpLength; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(OTP_CHARS.length());
            otp.append(OTP_CHARS.charAt(randomIndex));
        }

        return otp.toString();
    }
}