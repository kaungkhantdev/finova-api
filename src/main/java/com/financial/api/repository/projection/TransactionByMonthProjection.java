package com.financial.api.repository.projection;

import java.math.BigDecimal;

public interface TransactionByMonthProjection {
    Integer getMonth();
    BigDecimal getExpense();
    BigDecimal getIncome();
}
