package com.financial.api.repository.projection;

import java.math.BigDecimal;

public interface MonthlyComparisonProjection {
    BigDecimal getCurrentIncome();
    BigDecimal getPreviousIncome();
    BigDecimal getIncomeDifference();
    BigDecimal getIncomeChangePercent();

    BigDecimal getCurrentExpense();
    BigDecimal getPreviousExpense();
    BigDecimal getExpenseDifference();
    BigDecimal getExpenseChangePercent();
}
