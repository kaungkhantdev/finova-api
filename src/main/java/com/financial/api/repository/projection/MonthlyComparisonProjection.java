package com.financial.api.repository.projection;

import java.math.BigDecimal;

public interface MonthlyComparisonProjection {
    BigDecimal getCurrentIncome();
    BigDecimal getPreviousIncome();
    BigDecimal getIncomeChangePercent();
    BigDecimal getCurrentExpense();
    BigDecimal getPreviousExpense();
    BigDecimal getExpenseChangePercent();
}
