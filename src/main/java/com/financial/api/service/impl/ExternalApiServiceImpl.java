package com.financial.api.service.impl;

import com.financial.api.dto.response.ExchangeRateMatrixResponse;
import com.financial.api.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

    private final RestClient restClient;

    // Default target currencies
    private static final String DEFAULT_TARGET_CURRENCIES = "USD,EUR,GBP";

    @Override
    public ExchangeRateMatrixResponse getExchangeRateMatrix(String fromCurrency, String toCurrencies) {
        log.info("Fetching exchange rates from {} to {}", fromCurrency, toCurrencies);

        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/fetch-matrix")
                            .queryParam("from", fromCurrency)
                            .queryParam("to", toCurrencies)
                            .build())
                    .retrieve()
                    .body(ExchangeRateMatrixResponse.class);
        } catch (Exception e) {
            log.error("Failed to fetch exchange rates", e);
            throw new RuntimeException("Failed to fetch exchange rates from FastForex API", e);
        }
    }


    @Override
    public BigDecimal convertCurrency(String from, String to, BigDecimal amount) {
        log.info("Converting {} {} to {}", amount, from, to);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        ExchangeRateMatrixResponse response = getExchangeRateMatrix(from, to);

        Map<String, BigDecimal> rates = response.getMatrix().get(from);
        if (rates == null || !rates.containsKey(to)) {
            throw new RuntimeException("Exchange rate not found for " + from + " to " + to);
        }

        BigDecimal exchangeRate = rates.get(to);
        BigDecimal convertedAmount = amount.multiply(exchangeRate);

        log.info("Converted {} {} to {} {} (rate: {})", amount, from, convertedAmount, to, exchangeRate);

        return convertedAmount;
    }
}