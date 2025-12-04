package com.finova.api.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface WeeklyAmountProjection {
    BigDecimal getWeeklyAmount();
    LocalDate getWeekStart();
    LocalDate getWeekEnd();
}
