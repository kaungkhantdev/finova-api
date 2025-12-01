package com.financial.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "Request object for creating a new Account. " +
                "Used when registering a new financial account with an initial balance, category, and currency."
)
public class AccountCreateRequest {

    @NotBlank(message = "Account name is required")
    @Size(min = 1, max = 100, message = "Account name must be between 1 and 100 characters")
    @Schema(
            description = "Name of the account",
            example = "KBank Main Account",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    @Schema(
            description = "Description or additional notes about the account",
            example = "This account is used for primary savings and bill payments."
    )
    private String description;
}
