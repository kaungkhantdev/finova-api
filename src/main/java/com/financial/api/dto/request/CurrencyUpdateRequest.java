package com.financial.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data()
public class CurrencyUpdateRequest {
    @Size(max = 100, message = "Currency name must not exceed 100 characters")
    private String currency;

    @Size(max = 10, message = "Currency code must not exceed 10 characters")
    private String code; // "USD", "EUR", "GBP"

    @Size(max = 10, message = "Currency symbol must not exceed 10 characters")
    private String symbol; // "$", "€", "£"
}
