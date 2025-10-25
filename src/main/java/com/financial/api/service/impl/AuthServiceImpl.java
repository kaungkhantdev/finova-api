package com.financial.api.service.impl;

import com.financial.api.dto.request.LoginRequest;
import com.financial.api.dto.request.RegisterRequest;
import com.financial.api.dto.request.RefreshTokenRequest;
import com.financial.api.dto.response.AuthResponse;
import com.financial.api.entity.Role;
import com.financial.api.entity.User;
import com.financial.api.repository.RoleRepository;
import com.financial.api.repository.UserRepository;
import com.financial.api.security.JwtTokenProvider;
import com.financial.api.service.AuthService;
import com.financial.api.util.EmailParser;
import com.financial.api.util.EmailParts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.financial.api.constant.SecurityConstants.ROLE_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailParser emailParser;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new Error("Email already exists");
        }

        EmailParts parse = emailParser.parse(request.getEmail());

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
}