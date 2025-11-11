package com.financial.api.constant;

public class OtpConstants {
    public static final String OTP_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";
    public static final int OTP_EXPIRY_MINUTES = 10;
    public static final int MAX_ATTEMPTS = 3;
    public static final int OTP_LENGTH = 6;
    public static final int MIN_RESEND_INTERVAL_SECONDS = 60;
    public static final int LOCKOUT_DURATION_MINUTES = 15;
}

