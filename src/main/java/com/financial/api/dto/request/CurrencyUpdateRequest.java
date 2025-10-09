package com.financial.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for updating an existing currency")
public class CurrencyUpdateRequest {

    @Schema(
            description = "Name of the currency",
            example = "US Dollar",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            maxLength = 100
    )
    @Size(max = 100, message = "Currency name must not exceed 100 characters")
    private String currency;

    @Schema(
            description = "ISO 4217 currency code",
            example = "USD",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            maxLength = 10
    )
    @Size(max = 10, message = "Currency code must not exceed 10 characters")
    private String code;

    @Schema(
            description = "Currency symbol",
            example = "$",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            maxLength = 10
    )
    @Size(max = 10, message = "Currency symbol must not exceed 10 characters")
    private String symbol;
}