package com.finova.api.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal amount;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private Long accountId;
    private String accountName;
    private Long transactionTypeId;
    private String transactionTypeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
