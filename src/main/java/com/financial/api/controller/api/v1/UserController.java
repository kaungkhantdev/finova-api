package com.financial.api.controller.api.v1;

import com.financial.api.dto.response.UserResponse;
import com.financial.api.service.UserService;
import com.financial.api.util.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.financial.api.config.OpenApiConfig.BEARER_AUTH;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "TransactionType Management", description = "APIs for managing transaction type (supports both Cookie and Bearer token authentication)")
@SecurityRequirement(name = BEARER_AUTH)
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    @Operation(
            summary = "Get current user profile",
            description = "Retrieve current user profile. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<UserResponse>> getCurrentUserProfile() {
        UserResponse user = userService.getCurrentUserProfile();
        AppApiResponse<UserResponse> response = AppApiResponse.success(user);
        return ResponseEntity.ok(response);
    }
}
