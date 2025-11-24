package com.financial.api.repository.projection;

import java.math.BigDecimal;

public interface MonthlyAmountProjection {
    BigDecimal getMonthlyAmount();
    Integer getMonth();
}
