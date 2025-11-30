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
    private String currentIncome;
    private String previousIncome;
    private BigDecimal incomeChangePercent;
    private String incomeDifference;
    private String currentExpense;
    private String previousExpense;
    private BigDecimal expenseChangePercent;
    private String expenseDifference;
}
