package com.financial.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a new transaction type")
public class TransactionTypeCreateRequest {
    @Schema(
            description = "Name of the transaction type",
            example = "Income",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    @NotBlank(message = "Transaction type name is required")
    @Size(max = 100, message = "Transaction type name must not exceed 100 characters")
    private String name;

    @Schema(
            description = "Transaction Type Description",
            example = "Description",
            maxLength = 10
    )
    @Size(max = 250, message = "Description must not exceed 250 characters")
    private String description;
}
