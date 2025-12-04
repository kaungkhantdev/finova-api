package com.finova.api.service.impl;

import com.finova.api.dto.mapper.TransactionMapper;
import com.finova.api.dto.request.TransactionCreateRequest;
import com.finova.api.dto.request.TransactionUpdateRequest;
import com.finova.api.dto.response.*;
import com.finova.api.entity.*;
import com.finova.api.repository.*;
import com.finova.api.dto.response.*;
import com.finova.api.entity.*;
import com.finova.api.repository.AccountRepository;
import com.finova.api.repository.CategoryRepository;
import com.finova.api.repository.TransactionRepository;
import com.finova.api.repository.TransactionTypeRepository;
import com.finova.api.repository.projection.DailyAmountProjection;
import com.finova.api.repository.projection.MonthlyAmountProjection;
import com.finova.api.repository.projection.MonthlyComparisonProjection;
import com.finova.api.repository.projection.WeeklyAmountProjection;
import com.finova.api.service.strategy.TransactionBalanceStrategy;
import com.finova.api.service.strategy.TransactionBalanceStrategyFactory;
import com.finova.api.util.AuthenticationUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.finova.api.constant.TransactionConstants.EXPENSE_TYPE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionTypeRepository transactionTypeRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AuthenticationUtil authenticationUtil;

    @Mock
    private TransactionBalanceStrategyFactory strategyFactory;

    @Mock
    private TransactionBalanceStrategy balanceStrategy;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User user;
    private Account account;
    private Category category;
    private TransactionType transactionType;
    private Transaction transaction;
    private TransactionCreateRequest createRequest;
    private TransactionUpdateRequest updateRequest;
    private TransactionResponse transactionResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        account = new Account();
        account.setId(1L);
        account.setAmount(new BigDecimal("1000.00"));

        category = new Category();
        category.setId(1L);
        category.setName("Food");

        transactionType = new TransactionType();
        transactionType.setId(1L);
        transactionType.setName("EXPENSE");

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDescription("Test transaction");
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setTransactionType(transactionType);
        transaction.setIsDeleted(false);

        createRequest = new TransactionCreateRequest();
        createRequest.setAmount(new BigDecimal("100.00"));
        createRequest.setDescription("Test transaction");
        createRequest.setAccountId(1L);
        createRequest.setCategoryId(1L);
        createRequest.setTransactionTypeId(1L);

        updateRequest = new TransactionUpdateRequest();
        updateRequest.setAmount(new BigDecimal("150.00"));

        transactionResponse = new TransactionResponse();
        transactionResponse.setId(1L);
        transactionResponse.setAmount(new BigDecimal("100.00"));
    }

    @Test
    void getAll_WithKeyword_ShouldReturnPageOfTransactions() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "food";
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));

        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByUserAndIsDeletedFalse(user, keyword, pageable)).thenReturn(transactionPage);
        when(transactionMapper.toResponse(transaction)).thenReturn(transactionResponse);

        // When
        Page<TransactionResponse> result = transactionService.getAll(pageable, keyword);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).findByUserAndIsDeletedFalse(user, keyword, pageable);
    }

    @Test
    void getAll_WithoutKeyword_ShouldReturnPageOfTransactions() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));

        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByUserAndIsDeletedFalse(user, null, pageable)).thenReturn(transactionPage);
        when(transactionMapper.toResponse(transaction)).thenReturn(transactionResponse);

        // When
        Page<TransactionResponse> result = transactionService.getAll(pageable, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).findByUserAndIsDeletedFalse(user, null, pageable);
    }

    @Test
    void getTransactionById_WithValidId_ShouldReturnTransaction() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toResponse(transaction)).thenReturn(transactionResponse);

        // When
        TransactionResponse result = transactionService.getTransactionById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void getTransactionById_WithInvalidId_ShouldThrowException() {
        // Given
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.getTransactionById(999L));
    }

    @Test
    void createTransaction_WithValidRequest_ShouldCreateTransaction() {
        // Given
        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionTypeRepository.findById(1L)).thenReturn(Optional.of(transactionType));
        when(strategyFactory.getStrategy(EXPENSE_TYPE)).thenReturn(balanceStrategy);
        when(transactionMapper.toEntity(any(), any(), any(), any(), any())).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionMapper.toResponse(transaction)).thenReturn(transactionResponse);

        // When
        TransactionResponse result = transactionService.createTransaction(createRequest);

        // Then
        assertNotNull(result);
        verify(balanceStrategy).process(account, createRequest.getAmount());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void createTransaction_WithNegativeAmount_ShouldThrowException() {
        // Given
        createRequest.setAmount(new BigDecimal("-100.00"));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.createTransaction(createRequest));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_WithZeroAmount_ShouldThrowException() {
        // Given
        createRequest.setAmount(BigDecimal.ZERO);

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.createTransaction(createRequest));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void updateTransaction_WithValidRequest_ShouldUpdateTransaction() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(strategyFactory.getStrategy(EXPENSE_TYPE)).thenReturn(balanceStrategy);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionMapper.toResponse(transaction)).thenReturn(transactionResponse);

        // When
        TransactionResponse result = transactionService.updateTransaction(1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(balanceStrategy).revert(account, transaction.getAmount());
        verify(balanceStrategy).process(account, updateRequest.getAmount());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void updateTransaction_WithNewAccount_ShouldUpdateAccountBalances() {
        // Given
        Account newAccount = new Account();
        newAccount.setId(2L);
        newAccount.setAmount(new BigDecimal("2000.00"));

        updateRequest.setAccountId(2L);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(newAccount));
        when(strategyFactory.getStrategy(EXPENSE_TYPE)).thenReturn(balanceStrategy);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionMapper.toResponse(transaction)).thenReturn(transactionResponse);

        // When
        TransactionResponse result = transactionService.updateTransaction(1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(balanceStrategy).revert(account, transaction.getAmount());
        verify(balanceStrategy).process(newAccount, updateRequest.getAmount());
    }

    @Test
    void deleteTransaction_WithValidId_ShouldSoftDeleteTransaction() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(strategyFactory.getStrategy(EXPENSE_TYPE)).thenReturn(balanceStrategy);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // When
        transactionService.deleteTransaction(1L);

        // Then
        assertTrue(transaction.getIsDeleted());
        verify(balanceStrategy).revert(account, transaction.getAmount());
        verify(transactionRepository).save(transaction);
    }

    @Test
    void deleteTransaction_WithInvalidId_ShouldThrowException() {
        // Given
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.deleteTransaction(999L));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getTransactionByDate_WithDays_ShouldReturnTransactions() {
        // Given
        Integer days = 7;
        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(transactionRepository.getTransactionByDate(user.getId(), days)).thenReturn(List.of());

        // When
        List<TransactionByDateResponse> result = transactionService.getTransactionByDate(days);

        // Then
        assertNotNull(result);
        verify(transactionRepository).getTransactionByDate(user.getId(), days);
    }

    @Test
    void getTransactionByMonth_ShouldReturnMonthlyTransactions() {
        // Given
        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(transactionRepository.getTransactionByMonth(user.getId())).thenReturn(List.of());

        // When
        List<TransactionByMonthResponse> result = transactionService.getTransactionByMonth();

        // Then
        assertNotNull(result);
        verify(transactionRepository).getTransactionByMonth(user.getId());
    }

    @Test
    void getAmountPercentage_ShouldReturnAmountPercentageData() {
        // Given
        Long transactionTypeId = 1L;

        DailyAmountProjection dailyProjection = mock(DailyAmountProjection.class);
        when(dailyProjection.getDailyAmount()).thenReturn(new BigDecimal("100.00"));
        when(dailyProjection.getDate()).thenReturn(LocalDate.now());

        WeeklyAmountProjection weeklyProjection = mock(WeeklyAmountProjection.class);
        when(weeklyProjection.getWeeklyAmount()).thenReturn(new BigDecimal("700.00"));
        when(weeklyProjection.getWeekStart()).thenReturn(LocalDate.now().minusDays(7));
        when(weeklyProjection.getWeekEnd()).thenReturn(LocalDate.now());

        MonthlyAmountProjection monthlyProjection = mock(MonthlyAmountProjection.class);
        when(monthlyProjection.getMonthlyAmount()).thenReturn(new BigDecimal("3000.00"));
        when(monthlyProjection.getMonth()).thenReturn(12);

        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(transactionRepository.getTransactionsByCategory(user.getId(), transactionTypeId)).thenReturn(List.of());
        when(transactionRepository.getDailyAmount(user.getId(), transactionTypeId)).thenReturn(dailyProjection);
        when(transactionRepository.getWeeklyAmount(user.getId(), transactionTypeId)).thenReturn(weeklyProjection);
        when(transactionRepository.getMonthlyAmount(user.getId(), transactionTypeId)).thenReturn(monthlyProjection);

        // When
        AmountPercentageResponse result = transactionService.getAmountPercentage(transactionTypeId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getCategoryPercentage());
        assertNotNull(result.getDailyAmount());
        assertNotNull(result.getWeeklyAmount());
        assertNotNull(result.getMonthlyAmount());
        verify(transactionRepository).getTransactionsByCategory(user.getId(), transactionTypeId);
        verify(transactionRepository).getDailyAmount(user.getId(), transactionTypeId);
        verify(transactionRepository).getWeeklyAmount(user.getId(), transactionTypeId);
        verify(transactionRepository).getMonthlyAmount(user.getId(), transactionTypeId);
    }

    @Test
    void getMonthlyComparison_ShouldReturnComparisonData() {
        // Given
        MonthlyComparisonProjection projection = mock(MonthlyComparisonProjection.class);
        when(projection.getCurrentIncome()).thenReturn(new BigDecimal("5000.00"));
        when(projection.getPreviousIncome()).thenReturn(new BigDecimal("4500.00"));
        when(projection.getIncomeChangePercent()).thenReturn(new BigDecimal("11.11"));
        when(projection.getIncomeDifference()).thenReturn(new BigDecimal("500.00"));
        when(projection.getCurrentExpense()).thenReturn(new BigDecimal("3000.00"));
        when(projection.getPreviousExpense()).thenReturn(new BigDecimal("2800.00"));
        when(projection.getExpenseChangePercent()).thenReturn(new BigDecimal("7.14"));
        when(projection.getExpenseDifference()).thenReturn(new BigDecimal("200.00"));

        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(transactionRepository.getMonthlyComparison(user.getId())).thenReturn(projection);

        // When
        MonthlyComparisonResponse result = transactionService.getMonthlyComparison();

        // Then
        assertNotNull(result);
        verify(transactionRepository).getMonthlyComparison(user.getId());
    }

    @Test
    void createTransaction_WithNonExistentCategory_ShouldThrowException() {
        // Given
        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.createTransaction(createRequest));
    }

    @Test
    void createTransaction_WithNonExistentAccount_ShouldThrowException() {
        // Given
        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.createTransaction(createRequest));
    }

    @Test
    void createTransaction_WithNonExistentTransactionType_ShouldThrowException() {
        // Given
        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionTypeRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.createTransaction(createRequest));
    }

    @Test
    void mapDailyAmountToResponse_WithNullData_ShouldReturnDefaultResponse() {
        // Given
        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(transactionRepository.getTransactionsByCategory(user.getId(), 1L)).thenReturn(List.of());
        when(transactionRepository.getDailyAmount(user.getId(), 1L)).thenReturn(null);
        when(transactionRepository.getWeeklyAmount(user.getId(), 1L)).thenReturn(null);
        when(transactionRepository.getMonthlyAmount(user.getId(), 1L)).thenReturn(null);

        // When
        AmountPercentageResponse result = transactionService.getAmountPercentage(1L);

        // Then
        assertNotNull(result);
        assertNotNull(result.getDailyAmount());
        assertEquals("0", result.getDailyAmount().getDailyAmount());
    }

    @Test
    void mapMonthlyComparisonToResponse_WithNullData_ShouldReturnDefaultResponse() {
        // Given
        when(authenticationUtil.getCurrentUser()).thenReturn(user);
        when(transactionRepository.getMonthlyComparison(user.getId())).thenReturn(null);

        // When
        MonthlyComparisonResponse result = transactionService.getMonthlyComparison();

        // Then
        assertNotNull(result);
        assertEquals("0", result.getCurrentIncome());
        assertEquals("0", result.getPreviousIncome());
        assertEquals(BigDecimal.ZERO, result.getIncomeChangePercent());
    }
}