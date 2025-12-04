package com.finova.api.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating a new transaction")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionCreateRequest {
    @NotBlank(message = "Transaction name is required")
    @Size(min = 1, max = 100, message = "Transaction name must be between 1 and 100 characters")
    @Schema(
            description = "Name of the transaction",
            example = "KBank Main Transaction",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    @Schema(
            description = "Description or additional notes about the account",
            example = "This account is used for primary savings and bill payments."
    )
    private String description;

    @NotNull(message = "Initial amount is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Amount cannot be negative")
    @Digits(integer = 17, fraction = 2, message = "Amount must have at most 17 integer digits and 2 decimal places")
    @Schema(
            description = "Initial balance or amount of the account",
            example = "5000.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal amount;

    @NotNull(message = "Transaction type ID is required")
    @Schema(
            description = "Reference ID of the associated transaction type",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long transactionTypeId;

    @NotNull(message = "Account ID is required")
    @Schema(
            description = "Reference ID of the associated account",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long accountId;

    @NotNull(message = "Category ID is required")
    @Schema(
            description = "Reference ID of the associated category",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long categoryId;
}