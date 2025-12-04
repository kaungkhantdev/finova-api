package com.finova.api.service;

import com.finova.api.dto.response.ExchangeRateMatrixResponse;
import com.finova.api.dto.response.MultiCurrencyConversionResponse;

import java.math.BigDecimal;

public interface ExternalApiService {

    ExchangeRateMatrixResponse getExchangeRateMatrix(String fromCurrency, String toCurrencies);

    BigDecimal convertCurrency(String from, String to, BigDecimal amount);

    MultiCurrencyConversionResponse convertToMultipleCurrencies(String from, BigDecimal amount);
}