package com.financial.api.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class AccountWithTotalsResponse {
    private Long accountId;
    private String accountName;
    private String currency;
    private String currencyCode;
    private String currencySymbol;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
}
