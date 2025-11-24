package com.financial.api.service;

import com.financial.api.dto.response.ExchangeRateMatrixResponse;

import java.math.BigDecimal;

public interface ExternalApiService {

    ExchangeRateMatrixResponse getExchangeRateMatrix(String fromCurrency, String toCurrencies);

    BigDecimal convertCurrency(String from, String to, BigDecimal amount);
}