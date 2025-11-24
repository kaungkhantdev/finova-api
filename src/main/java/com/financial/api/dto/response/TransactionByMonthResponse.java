package com.financial.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionByMonthResponse {
    private String month;
    private Double expense;
    private Double income;
}
