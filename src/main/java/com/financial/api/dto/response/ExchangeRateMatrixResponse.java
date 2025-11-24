package com.financial.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateMatrixResponse {

    @JsonProperty("from")
    private List<String> from;

    @JsonProperty("to")
    private Map<String, String> to;

    @JsonProperty("matrix")
    private Map<String, Map<String, BigDecimal>> matrix;

    @JsonProperty("updated")
    private String updated;

    @JsonProperty("calls")
    private Integer calls;

    @JsonProperty("ms")
    private Integer ms;
}