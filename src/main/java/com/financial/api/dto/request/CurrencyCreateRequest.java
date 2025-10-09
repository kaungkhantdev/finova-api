package com.financial.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a new currency")
public class CurrencyCreateRequest {

    @Schema(
            description = "Name of the currency",
            example = "US Dollar",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100
    )
    @NotBlank(message = "Currency name is required")
    @Size(max = 100, message = "Currency name must not exceed 100 characters")
    private String currency;

    @Schema(
            description = "ISO 4217 currency code",
            example = "USD",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 10
    )
    @NotBlank(message = "Currency code is required")
    @Size(max = 10, message = "Currency code must not exceed 10 characters")
    private String code;

    @Schema(
            description = "Currency symbol",
            example = "$",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 10
    )
    @NotBlank(message = "Currency symbol is required")
    @Size(max = 10, message = "Currency symbol must not exceed 10 characters")
    private String symbol;
}