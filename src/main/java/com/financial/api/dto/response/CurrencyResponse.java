package com.financial.api.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data()
public class CurrencyResponse {
    private Long id;
    private String currency;
    private String code;
    private String symbol;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
