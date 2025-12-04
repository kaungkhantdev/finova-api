package com.finova.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @Schema(
            description = "Email",
            example = "test@gmail.co",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Password",
            example = "your-password",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @Schema(
            description = "Name",
            example = "your-name",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    private String name;


    @NotNull(message = "Currency ID is required")
    @JsonProperty("currency_id")
    @Schema(
            description = "Reference ID of the associated currency",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long currencyId;
}
