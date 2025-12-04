package com.finova.api.repository.projection;

import java.math.BigDecimal;

public interface TransactionByCategoryProjection {
    String getCategoryName();
    Integer getTransactionCount();
    BigDecimal getTotalAmount();
    BigDecimal getPercent();
}
