package com.finova.api.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.finova.api.repository.projection.TransactionByCategoryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionByCategoryResponse {
    private String categoryName;
    private Integer transactionCount;
    private BigDecimal totalAmount;
    private BigDecimal percent;

    public TransactionByCategoryResponse(TransactionByCategoryProjection projection) {
        this.categoryName = projection.getCategoryName();
        this.transactionCount = projection.getTransactionCount();
        this.totalAmount = projection.getTotalAmount();
        this.percent = projection.getPercent();
    }
}
