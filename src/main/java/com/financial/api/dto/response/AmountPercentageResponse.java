package com.financial.api.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AmountPercentageResponse {
    private List<TransactionByCategoryResponse> categoryPercentage;
    private DailyAmountResponse dailyAmount;
    private WeeklyAmountResponse weeklyAmount;
    private MonthlyAmountResponse monthlyAmount;
}
