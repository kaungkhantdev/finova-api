package com.financial.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data()
public class CurrencyCreateRequest {
    @NotBlank(message = "Currency name is required")
    @Size(max = 100, message = "Currency name must not exceed 100 characters")
    private String currency;

    @NotBlank(message = "Currency code is required")
    @Size(max = 10, message = "Currency code must not exceed 10 characters")
    private String code; // "USD", "EUR", "GBP"

    @Size(max = 10, message = "Currency symbol must not exceed 10 characters")
    private String symbol; // "$", "€", "£"
}
