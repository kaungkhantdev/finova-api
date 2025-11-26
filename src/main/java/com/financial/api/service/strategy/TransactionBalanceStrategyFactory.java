package com.financial.api.service.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionBalanceStrategyFactory {

    private final Map<String, TransactionBalanceStrategy> strategies;

    public TransactionBalanceStrategy getStrategy(String transactionTypeName) {
        TransactionBalanceStrategy strategy = strategies.get(transactionTypeName);

        if (strategy == null) {
            log.error("No strategy found for transaction type: {}", transactionTypeName);
            throw new IllegalArgumentException("Unsupported transaction type: " + transactionTypeName);
        }

        return strategy;
    }
}