package com.finova.api.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionByDateProjection {
    LocalDate getDate();
    BigDecimal getIncome();
    BigDecimal getExpense();
}
