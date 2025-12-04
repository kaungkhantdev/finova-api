package com.finova.api.dto.response;

import com.finova.api.repository.projection.TransactionByMonthProjection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionByMonthResponse {
    private Integer month;
    private BigDecimal expense;
    private BigDecimal income;

    public TransactionByMonthResponse(TransactionByMonthProjection projection) {
        this.month = projection.getMonth();
        this.income = projection.getIncome();
        this.expense = projection.getExpense();
    }
}
