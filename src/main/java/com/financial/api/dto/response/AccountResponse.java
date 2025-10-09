package com.financial.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    private Long id;

    private String name;

    private String description;

    private BigDecimal amount;

    private Long userId;

    private Long categoryId;

    private String categoryName;

    private Long currencyId;

    private String currencyCode;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}