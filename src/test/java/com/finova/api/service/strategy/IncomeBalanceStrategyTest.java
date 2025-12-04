package com.finova.api.service.strategy;

import com.finova.api.entity.Account;
import com.finova.api.exception.InsufficientBalanceException;
import com.finova.api.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeBalanceStrategyTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private IncomeBalanceStrategy incomeBalanceStrategy;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAmount(new BigDecimal("1000.00"));
    }

    @Test
    void process_ShouldAddAmount() {
        // Given
        BigDecimal incomeAmount = new BigDecimal("500.00");
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // When
        incomeBalanceStrategy.process(account, incomeAmount);

        // Then
        assertEquals(new BigDecimal("1500.00"), account.getAmount());
        verify(accountRepository).save(account);
    }

    @Test
    void revert_WithSufficientBalance_ShouldDeductAmount() {
        // Given
        BigDecimal incomeAmount = new BigDecimal("500.00");
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // When
        incomeBalanceStrategy.revert(account, incomeAmount);

        // Then
        assertEquals(new BigDecimal("500.00"), account.getAmount());
        verify(accountRepository).save(account);
    }

    @Test
    void revert_WithInsufficientBalance_ShouldThrowException() {
        // Given
        BigDecimal incomeAmount = new BigDecimal("1500.00");

        // When & Then
        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> incomeBalanceStrategy.revert(account, incomeAmount)
        );

        assertTrue(exception.getMessage().contains("Insufficient balance"));
        verify(accountRepository, never()).save(any());
    }
}