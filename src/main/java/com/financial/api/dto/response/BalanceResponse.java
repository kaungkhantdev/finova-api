package com.financial.api.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BalanceResponse extends MultiCurrencyConversionResponse {
    private String fromCurrencySymbol;
    private String formattedOriginalAmount;
    private Map<String, String> formattedConversions;

    public BalanceResponse(MultiCurrencyConversionResponse base) {
        super(
                base.getFromCurrency(),
                base.getOriginalAmount(),
                base.getConversions(),
                base.getLastUpdated()
        );
    }
}
