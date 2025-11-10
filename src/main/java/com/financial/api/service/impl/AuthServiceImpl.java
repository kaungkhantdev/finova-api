package com.financial.api.service.impl;

import com.financial.api.dto.request.*;
import com.financial.api.dto.response.AuthResponse;
import com.financial.api.entity.OtpCode;
import com.financial.api.entity.Role;
import com.financial.api.entity.User;
import com.financial.api.repository.OtpCodeRepository;
import com.financial.api.repository.RoleRepository;
import com.financial.api.repository.UserRepository;
import com.financial.api.security.JwtTokenProvider;
import com.financial.api.service.AuthService;
import com.financial.api.service.MailService;
import com.financial.api.util.GenerateOtp;
import com.financial.api.util.MailParser;
import com.financial.api.util.MailParts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.financial.api.constant.OtpConstants.MAX_ATTEMPTS;
import static com.financial.api.constant.OtpConstants.OTP_EXPIRY_MINUTES;
import static com.financial.api.constant.SecurityConstants.ROLE_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final MailParser mailParser;
    private final MailService mailService;
    private final GenerateOtp generateOtp;

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

            String otp = generateOtp.generateSecureOtp();
            String otpHash = passwordEncoder.encode(otp);

            OtpCode otpCode = new OtpCode();
            otpCode.setEmail(request.getEmail());
            otpCode.setOtpHash(otpHash);
            otpCode.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
            otpCode.setUsed(false);
            otpCode.setAttempts(0);

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
            throw new RuntimeException("Failed to send OTP: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean verifyOtp(VerifyOtpRequest request) {
        OtpCode otpCode = otpCodeRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("Otp Code not found with Email: " + request.getEmail()));

        if (otpCode.isUsed()) throw new Error("OTP already used.");
        if (otpCode.getExpiresAt().isBefore(LocalDateTime.now())) throw new Error("OTP expired.");
        if (otpCode.getAttempts() >= MAX_ATTEMPTS) throw new Error("Maximum attempts exceeded");

        boolean valid = passwordEncoder.matches(request.getOtp(), otpCode.getOtpHash());
        otpCode.setAttempts(otpCode.getAttempts() + 1);

        if (valid) {
            otpCode.setUsed(true);
        }

        otpCodeRepository.save(otpCode);
        return valid;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        boolean valid = verifyOtp(
                new VerifyOtpRequest(request.getEmail(), request.getOtp())
        );

        if (!valid) throw new Error("Invalid OTP");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new Error("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password reset successful for {}", request.getEmail());
    }
}