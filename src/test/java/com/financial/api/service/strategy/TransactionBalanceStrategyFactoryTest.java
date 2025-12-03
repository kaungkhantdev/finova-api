package com.financial.api.service.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static com.financial.api.constant.TransactionConstants.EXPENSE_TYPE;
import static com.financial.api.constant.TransactionConstants.INCOME_TYPE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TransactionBalanceStrategyFactoryTest {

    @Mock
    private TransactionBalanceStrategy expenseStrategy;

    @Mock
    private TransactionBalanceStrategy incomeStrategy;

    private TransactionBalanceStrategyFactory factory;

    @BeforeEach
    void setUp() {
        Map<String, TransactionBalanceStrategy> strategies = new HashMap<>();
        strategies.put(EXPENSE_TYPE, expenseStrategy);
        strategies.put(INCOME_TYPE, incomeStrategy);
        factory = new TransactionBalanceStrategyFactory(strategies);
    }

    @Test
    void getStrategy_WithValidExpenseType_ShouldReturnExpenseStrategy() {
        // When
        TransactionBalanceStrategy strategy = factory.getStrategy(EXPENSE_TYPE);

        // Then
        assertNotNull(strategy);
        assertEquals(expenseStrategy, strategy);
    }

    @Test
    void getStrategy_WithValidIncomeType_ShouldReturnIncomeStrategy() {
        // When
        TransactionBalanceStrategy strategy = factory.getStrategy(INCOME_TYPE);

        // Then
        assertNotNull(strategy);
        assertEquals(incomeStrategy, strategy);
    }

    @Test
    void getStrategy_WithInvalidType_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.getStrategy("INVALID")
        );

        assertTrue(exception.getMessage().contains("Unsupported transaction type"));
    }
}