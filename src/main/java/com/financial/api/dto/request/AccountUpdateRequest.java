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
        description = "Request object for updating an existing Account. " +
                "All fields are optional but must follow validation rules if provided."
)
public class AccountUpdateRequest {

    @Size(min = 1, max = 100, message = "Account name must be between 1 and 100 characters")
    @Schema(
            description = "Updated name of the account",
            example = "Updated KBank Account"
    )
    private String name;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    @Schema(
            description = "Updated description or notes for the account",
            example = "Updated description for clarity."
    )
    private String description;
}
