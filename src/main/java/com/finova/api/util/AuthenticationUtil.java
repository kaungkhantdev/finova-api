package com.finova.api.util;

import com.finova.api.entity.User;
import com.finova.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class for handling authentication-related operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final UserRepository userRepository;

    /**
     * Retrieves the current authenticated user's ID.
     *
     * @return user ID
     * @throws IllegalStateException if no authenticated user found
     */
    public Long getCurrentUserId() {
        String username = getCurrentUsername();

        if (username == null) {
            log.error("No authenticated user found in security context");
            throw new IllegalStateException("No authenticated user found");
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", username);
                    return new IllegalStateException("Authenticated user not found in database");
                });

        return user.getId();
    }

    /**
     * Retrieves the current authenticated user entity.
     *
     * @return User entity
     * @throws IllegalStateException if no authenticated user found
     */
    public User getCurrentUser() {
        String username = getCurrentUsername();

        if (username == null) {
            log.error("No authenticated user found in security context");
            throw new IllegalStateException("No authenticated user found");
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", username);
                    return new IllegalStateException("Authenticated user not found in database");
                });
    }

    public User getCurrentUserWithCurrency() {
        String username = getCurrentUsername();

        if (username == null) {
            log.error("No authenticated user found in security context");
            throw new IllegalStateException("No authenticated user found");
        }

        return userRepository.findByEmailWithCurrency(username)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", username);
                    return new IllegalStateException("Authenticated user not found in database");
                });
    }

    /**
     * Gets the current user's username (email).
     *
     * @return username (email)
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }

        log.warn("Unknown principal type: {}", principal.getClass().getName());
        return null;
    }

    /**
     * Checks if there is an authenticated user.
     *
     * @return true if user is authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     * Checks if the current user has a specific role.
     *
     * @param roleName role name to check
     * @return true if user has the role
     */
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleName));
    }
}