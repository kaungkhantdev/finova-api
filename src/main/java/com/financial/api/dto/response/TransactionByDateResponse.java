package com.financial.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionByDateResponse {
    private String date;
    private Double expense;
    private Double income;
}
