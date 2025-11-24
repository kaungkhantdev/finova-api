package com.financial.api.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyAmountProjection {
    BigDecimal getDailyAmount();
    LocalDate getDate();
}
