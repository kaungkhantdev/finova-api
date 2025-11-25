package com.financial.api.service.impl;

import com.financial.api.dto.response.ExchangeRateMatrixResponse;
import com.financial.api.dto.response.MultiCurrencyConversionResponse;
import com.financial.api.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static com.financial.api.constant.TransactionConstants.DEFAULT_TARGET_CURRENCIES;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

    private final RestClient restClient;

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
        BigDecimal convertedAmount = amount.multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP);

        log.info("Converted {} {} to {} {} (rate: {})", amount, from, convertedAmount, to, exchangeRate);

        return convertedAmount;
    }

    @Override
    public MultiCurrencyConversionResponse convertToMultipleCurrencies(String from, BigDecimal amount) {
        log.info("Converting {} {} to multiple currencies (USD, EUR, GBP)", amount, from);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Fetch exchange rates for USD, EUR, GBP
        ExchangeRateMatrixResponse response = getExchangeRateMatrix(from, DEFAULT_TARGET_CURRENCIES);

        Map<String, BigDecimal> rates = response.getMatrix().get(from);
        if (rates == null || rates.isEmpty()) {
            throw new RuntimeException("No exchange rates found for currency: " + from);
        }

        // Calculate conversions for each target currency
        Map<String, BigDecimal> conversions = new java.util.HashMap<>();

        rates.forEach((currency, rate) -> {
            BigDecimal convertedAmount = amount.multiply(rate)
                    .setScale(2, RoundingMode.HALF_UP);
            conversions.put(currency, convertedAmount);
            log.info("Converted {} {} to {} {} (rate: {})", amount, from, convertedAmount, currency, rate);
        });

        return new MultiCurrencyConversionResponse(
                from,
                amount,
                conversions,
                response.getUpdated()
        );
    }
}