package com.financial.api.service.impl;

import com.financial.api.dto.request.*;
import com.financial.api.dto.response.AuthResponse;
import com.financial.api.entity.Currency;
import com.financial.api.entity.OtpCode;
import com.financial.api.entity.Role;
import com.financial.api.entity.User;
import com.financial.api.repository.CurrencyRepository;
import com.financial.api.repository.OtpCodeRepository;
import com.financial.api.repository.RoleRepository;
import com.financial.api.repository.UserRepository;
import com.financial.api.security.JwtTokenProvider;
import com.financial.api.service.AuthService;
import com.financial.api.service.MailService;
import com.financial.api.util.GenerateOtp;
import com.financial.api.util.MailParser;
import com.financial.api.util.MailParts;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.financial.api.constant.OtpConstants.*;
import static com.financial.api.constant.SecurityConstants.ROLE_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;
    private final RoleRepository roleRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final MailParser mailParser;
    private final MailService mailService;
    private final GenerateOtp generateOtp;

    @Value("${jwt.reset_expiration}")
    private int resetExpirationInMs;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new Error("Email already exists");
        }

        MailParts parse = mailParser.parse(request.getEmail());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setUsername(parse.getUserName());
        user.setCurrency(getCurrency(request.getCurrencyId()));
        Role role = roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new IllegalArgumentException("Role USER not found"));
        user.getRoles().add(role);

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600) // 1 hour
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new Error("User not found"));

        // Update last login
//        user.setLastLogin(LocalDateTime.now());
//        user.setFailedLoginAttempts(0);
//        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600) // 1 hour
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String userEmail = jwtTokenProvider.extractUsername(request.getRefreshToken());

        if (userEmail != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtTokenProvider.isTokenValid(request.getRefreshToken(), userDetails)) {
                String accessToken = jwtTokenProvider.generateToken(userDetails);

                return AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(request.getRefreshToken())
                        .tokenType("Bearer")
                        .expiresIn(3600) // 1 hour
                        .build();
            }
        }

        throw new Error("Invalid refresh token");
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new NoSuchElementException("User not found with Email: " + request.getEmail()));

            // Check for recent OTP requests (rate limiting)
            Optional<OtpCode> recentOtp = otpCodeRepository
                    .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(request.getEmail());

            if (recentOtp.isPresent()) {
                OtpCode existingOtp = recentOtp.get();
                long secondsAgo = Duration.between(existingOtp.getCreatedAt(), LocalDateTime.now()).getSeconds();

                if (secondsAgo < MIN_RESEND_INTERVAL_SECONDS) {
                    long waitSeconds = MIN_RESEND_INTERVAL_SECONDS - secondsAgo;
                    throw new IllegalStateException("Please wait " + waitSeconds + " seconds before requesting a new OTP");
                }

                // Mark old OTP as used
                existingOtp.setUsed(true);
                otpCodeRepository.save(existingOtp);
            }

            // Generate new OTP
            String otp = generateOtp.generateSecureOtp();
            String otpHash = passwordEncoder.encode(otp);

            OtpCode otpCode = new OtpCode();
            otpCode.setEmail(request.getEmail());
            otpCode.setOtpHash(otpHash);
            otpCode.setCreatedAt(LocalDateTime.now()); // IMPORTANT: Add this
            otpCode.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
            otpCode.setUsed(false);
            otpCode.setAttempts(0);
            otpCode.setLockedUntil(null); // Reset lock

            otpCodeRepository.save(otpCode);

            // Send OTP email
            mailService.sendMail(
                    MailRequest.builder()
                            .to(request.getEmail())
                            .subject("Password Reset OTP")
                            .templateName("mail/otp")
                            .variables(Map.of(
                                    "userName", user.getName(),
                                    "otpCode", otp,
                                    "expiry", OTP_EXPIRY_MINUTES
                            ))
                            .build()
            );

            log.info("OTP sent to {}", request.getEmail());
        } catch (Exception e) {
            log.error("Failed to send OTP: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> verifyOtp(VerifyOtpRequest request) {
        OtpCode otpCode = otpCodeRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("No OTP found. Please request a new one."));

        // Check if locked FIRST
        if (otpCode.getLockedUntil() != null &&
                otpCode.getLockedUntil().isAfter(LocalDateTime.now())) {
            long minutesLeft = Duration.between(LocalDateTime.now(), otpCode.getLockedUntil()).toMinutes() + 1;
            throw new IllegalStateException(
                    "Too many failed attempts. Please wait " + minutesLeft + " minutes or request a new OTP."
            );
        }

        // Check if used
        if (otpCode.isUsed()) {
            throw new IllegalStateException("OTP already used. Please request a new one.");
        }

        // Check if expired
        if (otpCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpCode.setUsed(true);
            otpCodeRepository.save(otpCode);
            throw new IllegalStateException("OTP expired. Please request a new one.");
        }

        // Check max attempts
        if (otpCode.getAttempts() >= MAX_ATTEMPTS) {
            otpCode.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
            otpCode.setUsed(true); // Also mark as used
            otpCodeRepository.save(otpCode);
            throw new IllegalStateException(
                    "Maximum attempts exceeded. Your OTP is locked for " + LOCKOUT_DURATION_MINUTES +
                            " minutes. Please request a new OTP."
            );
        }

        // Verify OTP
        boolean valid = passwordEncoder.matches(request.getOtp(), otpCode.getOtpHash());
        otpCode.setAttempts(otpCode.getAttempts() + 1);

        if (!valid) {
            otpCodeRepository.save(otpCode);
            int remainingAttempts = MAX_ATTEMPTS - otpCode.getAttempts();
            throw new IllegalArgumentException(
                    "Invalid OTP. " + remainingAttempts + " attempt(s) remaining."
            );
        }

        // Mark as used and save
        otpCode.setUsed(true);
        otpCodeRepository.save(otpCode);

        // Generate reset token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String resetToken = jwtTokenProvider.generateResetToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("reset_token", resetToken);
        response.put("expire_in", resetExpirationInMs + " ms");

        log.info("OTP verified successfully for {}", request.getEmail());
        return response;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String userEmail = jwtTokenProvider.extractUsername(request.getResetToken());

        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("Invalid reset token");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (!jwtTokenProvider.isTokenValid(request.getResetToken(), userDetails)) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password reset successful for {}", userEmail);
    }

    private Currency getCurrency(Long currencyId) {
        return currencyRepository.findById(currencyId)
                .orElseThrow(() -> new EntityNotFoundException("Currency not found with ID: " + currencyId));
    }
}