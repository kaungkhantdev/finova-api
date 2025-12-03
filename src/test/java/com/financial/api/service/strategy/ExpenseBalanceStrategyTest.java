package com.financial.api.service.strategy;

import com.financial.api.entity.Account;
import com.financial.api.exception.InsufficientBalanceException;
import com.financial.api.repository.AccountRepository;
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
class ExpenseBalanceStrategyTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private ExpenseBalanceStrategy expenseBalanceStrategy;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAmount(new BigDecimal("1000.00"));
    }

    @Test
    void process_WithSufficientBalance_ShouldDeductAmount() {
        // Given
        BigDecimal expenseAmount = new BigDecimal("500.00");
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // When
        expenseBalanceStrategy.process(account, expenseAmount);

        // Then
        assertEquals(new BigDecimal("500.00"), account.getAmount());
        verify(accountRepository).save(account);
    }

    @Test
    void process_WithInsufficientBalance_ShouldThrowException() {
        // Given
        BigDecimal expenseAmount = new BigDecimal("1500.00");

        // When & Then
        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> expenseBalanceStrategy.process(account, expenseAmount)
        );

        assertTrue(exception.getMessage().contains("Insufficient balance"));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void process_WithExactBalance_ShouldDeductToZero() {
        // Given
        BigDecimal expenseAmount = new BigDecimal("1000.00");
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // When
        expenseBalanceStrategy.process(account, expenseAmount);

        // Then
        assertEquals(BigDecimal.ZERO.setScale(2), account.getAmount().setScale(2));
        verify(accountRepository).save(account);
    }

    @Test
    void revert_ShouldAddAmountBack() {
        // Given
        BigDecimal expenseAmount = new BigDecimal("200.00");
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // When
        expenseBalanceStrategy.revert(account, expenseAmount);

        // Then
        assertEquals(new BigDecimal("1200.00"), account.getAmount());
        verify(accountRepository).save(account);
    }
}