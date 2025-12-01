package com.financial.api.repository.projection;

import java.math.BigDecimal;

public interface AccountWithTotalsProjection {
    Long getAccountId();
    String getAccountName();
    String getCurrency();
    String getCurrencyCode();
    String getCurrencySymbol();
    BigDecimal getTotalIncome();
    BigDecimal getTotalExpense();
}
