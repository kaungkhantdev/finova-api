package com.financial.api.dto.response;

import com.financial.api.repository.projection.TransactionByDateProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionByDateResponse {
    private String date;
    private BigDecimal expense;
    private BigDecimal income;

    public TransactionByDateResponse(TransactionByDateProjection projection) {
        this.date = projection.getDate() != null ? projection.getDate().toString() : null;
        this.expense = projection.getExpense();
        this.income = projection.getIncome();
    }
}
