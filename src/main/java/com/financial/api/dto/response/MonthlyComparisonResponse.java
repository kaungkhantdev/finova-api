package com.financial.api.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MonthlyComparisonResponse {
    private BigDecimal currentIncome;
    private BigDecimal previousIncome;
    private BigDecimal incomeChangePercent;
    private BigDecimal currentExpense;
    private BigDecimal previousExpense;
    private BigDecimal expenseChangePercent;
}
