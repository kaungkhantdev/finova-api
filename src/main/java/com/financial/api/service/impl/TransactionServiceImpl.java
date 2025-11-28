package com.financial.api.service.impl;

import com.financial.api.dto.mapper.TransactionMapper;
import com.financial.api.dto.request.TransactionCreateRequest;
import com.financial.api.dto.request.TransactionUpdateRequest;
import com.financial.api.dto.response.*;
import com.financial.api.entity.*;
import com.financial.api.repository.AccountRepository;
import com.financial.api.repository.CategoryRepository;
import com.financial.api.repository.TransactionRepository;
import com.financial.api.repository.TransactionTypeRepository;
import com.financial.api.repository.projection.DailyAmountProjection;
import com.financial.api.repository.projection.MonthlyAmountProjection;
import com.financial.api.repository.projection.MonthlyComparisonProjection;
import com.financial.api.repository.projection.WeeklyAmountProjection;
import com.financial.api.service.TransactionService;
import com.financial.api.service.strategy.TransactionBalanceStrategyFactory;
import com.financial.api.service.strategy.TransactionBalanceStrategy;
import com.financial.api.util.AuthenticationUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final AuthenticationUtil authenticationUtil;
    private final TransactionBalanceStrategyFactory strategyFactory;

    @Transactional(readOnly = true)
    @Override
    public Page<TransactionResponse> getAll(Pageable pageable) {
        User currentUser = getCurrentUser();
        log.debug("Fetching all transactions for user: {}", currentUser.getId());

        Page<Transaction> transactions = transactionRepository.findByUserAndIsDeletedFalse(currentUser, pageable);
        return transactions.map(transactionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public TransactionResponse getTransactionById(Long id) {
        log.debug("Fetching transaction with ID: {}", id);
        Transaction transaction = findTransactionById(id);
        return transactionMapper.toResponse(transaction);
    }

    @Transactional
    @Override
    public TransactionResponse createTransaction(TransactionCreateRequest request) {
        log.info("Creating new transaction for amount: {}", request.getAmount());

        validatePositiveAmount(request.getAmount());

        User currentUser = getCurrentUser();
        Category category = getCategory(request.getCategoryId());
        Account account = getAccount(request.getAccountId());
        TransactionType transactionType = getTransactionType(request.getTransactionTypeId());

        processAccountBalance(account, transactionType, request.getAmount());

        Transaction transaction = transactionMapper.toEntity(
                request, currentUser, transactionType, account, category
        );
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Transaction created successfully with ID: {}", savedTransaction.getId());
        return transactionMapper.toResponse(savedTransaction);
    }

    @Transactional
    @Override
    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request) {
        log.info("Updating transaction with ID: {}", id);

        if (request.getAmount() != null) {
            validatePositiveAmount(request.getAmount());
        }

        Transaction existingTransaction = findTransactionById(id);

        // Load optional entities
        Category newCategory = loadOptionalCategory(request.getCategoryId());
        Account newAccount = loadOptionalAccount(request.getAccountId());
        TransactionType newTransactionType = loadOptionalTransactionType(request.getTransactionTypeId());

        // Capture original values
        Account originalAccount = existingTransaction.getAccount();
        TransactionType originalType = existingTransaction.getTransactionType();
        BigDecimal originalAmount = existingTransaction.getAmount();

        // Determine target values (use new if provided, otherwise keep original)
        Account targetAccount = newAccount != null ? newAccount : originalAccount;
        TransactionType targetType = newTransactionType != null ? newTransactionType : originalType;
        BigDecimal targetAmount = request.getAmount() != null ? request.getAmount() : originalAmount;

        // Revert original balance impact
        revertAccountBalance(originalAccount, originalType, originalAmount);

        // Apply new balance impact
        processAccountBalance(targetAccount, targetType, targetAmount);

        // Update entity fields
        transactionMapper.updateEntity(request, existingTransaction, newTransactionType, newAccount, newCategory);

        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        log.info("Transaction updated successfully with ID: {}", updatedTransaction.getId());

        return transactionMapper.toResponse(updatedTransaction);
    }

    @Transactional
    @Override
    public void deleteTransaction(Long id) {
        log.info("Soft deleting transaction with ID: {}", id);

        Transaction transaction = findTransactionById(id);

        // Revert account balance before soft delete
        revertAccountBalance(
                transaction.getAccount(),
                transaction.getTransactionType(),
                transaction.getAmount()
        );

        transaction.setIsDeleted(true);
        transactionRepository.save(transaction);

        log.info("Transaction soft deleted successfully with ID: {}", id);
    }

    @Override
    public List<TransactionByDateResponse> getTransactionByDate(Integer days) {
        User currentUser = getCurrentUser();
        return transactionRepository.getTransactionByDate(currentUser.getId(), days)
                .stream()
                .map(TransactionByDateResponse::new)
                .toList();
    }

    @Override
    public List<TransactionByMonthResponse> getTransactionByMonth() {
        User currentUser = getCurrentUser();
        return transactionRepository.getTransactionByMonth(currentUser.getId())
                .stream()
                .map(TransactionByMonthResponse::new)
                .toList();
    }

    @Override
    public AmountPercentageResponse getAmountPercentage(Long transactionTypeId) {
        User currentUser = getCurrentUser();

        List<TransactionByCategoryResponse> categoryPercentage = transactionRepository.getTransactionsByCategory(currentUser.getId(), transactionTypeId)
                .stream()
                .map(TransactionByCategoryResponse::new)
                .toList();
        DailyAmountResponse dailyAmount = mapDailyAmountToResponse(transactionRepository.getDailyAmount(currentUser.getId(), transactionTypeId));
        WeeklyAmountResponse weeklyAmount = mapWeeklyAmountToResponse(transactionRepository.getWeeklyAmount(currentUser.getId(), transactionTypeId));
        MonthlyAmountResponse monthlyAmount = mapMonthlyAmountToResponse(transactionRepository.getMonthlyAmount(currentUser.getId(), transactionTypeId));

        return new AmountPercentageResponse(
                categoryPercentage,
                dailyAmount,
                weeklyAmount,
                monthlyAmount
        );
    }

    @Override
    public MonthlyComparisonResponse getMonthlyComparison() {
        User currentUser = getCurrentUser();
        MonthlyComparisonProjection data = transactionRepository.getMonthlyComparison(currentUser.getId());
        return mapMonthlyComparisonToResponse(data);
    }


    // ==================== Balance Processing Methods (Using Strategy) ====================

    private void processAccountBalance(Account account, TransactionType transactionType, BigDecimal amount) {
        TransactionBalanceStrategy strategy = strategyFactory.getStrategy(transactionType.getName());
        strategy.process(account, amount);
    }

    private void revertAccountBalance(Account account, TransactionType transactionType, BigDecimal amount) {
        TransactionBalanceStrategy strategy = strategyFactory.getStrategy(transactionType.getName());
        strategy.revert(account, amount);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid amount: {}", amount);
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }
    }

    // ==================== Entity Retrieval Methods ====================

    private Transaction findTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with ID: " + id));
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + categoryId));
    }

    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + accountId));
    }

    private TransactionType getTransactionType(Long transactionTypeId) {
        return transactionTypeRepository.findById(transactionTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction Type not found with ID: " + transactionTypeId));
    }

    // ==================== Mapping Methods ====================

    private DailyAmountResponse mapDailyAmountToResponse(DailyAmountProjection data) {
        if (data == null) {
            return new DailyAmountResponse(BigDecimal.ZERO, LocalDate.now());
        }
        BigDecimal amount = data.getDailyAmount() != null ? data.getDailyAmount() : BigDecimal.ZERO;
        return new DailyAmountResponse(amount, data.getDate());
    }

    private WeeklyAmountResponse mapWeeklyAmountToResponse(WeeklyAmountProjection data) {
        if (data == null) {
            return new WeeklyAmountResponse(BigDecimal.ZERO, LocalDate.now(), LocalDate.now());
        }
        BigDecimal amount = data.getWeeklyAmount() != null ? data.getWeeklyAmount() : BigDecimal.ZERO;
        return new WeeklyAmountResponse(amount, data.getWeekStart(), data.getWeekEnd());
    }

    private MonthlyAmountResponse mapMonthlyAmountToResponse(MonthlyAmountProjection data) {
        if (data == null) {
            return new MonthlyAmountResponse(BigDecimal.ZERO, LocalDate.now().getMonthValue());
        }
        BigDecimal amount = data.getMonthlyAmount() != null ? data.getMonthlyAmount() : BigDecimal.ZERO;
        return new MonthlyAmountResponse(amount, data.getMonth());
    }

    private MonthlyComparisonResponse mapMonthlyComparisonToResponse(MonthlyComparisonProjection data) {
        if (data == null) {
            return new MonthlyComparisonResponse(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );
        }
        return new MonthlyComparisonResponse(
                orZero(data.getCurrentIncome()),
                orZero(data.getPreviousIncome()),
                orZero(data.getIncomeChangePercent()),
                orZero(data.getCurrentExpense()),
                orZero(data.getPreviousExpense()),
                orZero(data.getExpenseChangePercent())
        );
    }

    private BigDecimal orZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    // ==================== Optional Entity Loading Methods ====================

    private Category loadOptionalCategory(Long categoryId) {
        return categoryId != null ? getCategory(categoryId) : null;
    }

    private Account loadOptionalAccount(Long accountId) {
        return accountId != null ? getAccount(accountId) : null;
    }

    private TransactionType loadOptionalTransactionType(Long transactionTypeId) {
        return transactionTypeId != null ? getTransactionType(transactionTypeId) : null;
    }

    private User getCurrentUser() {
        return authenticationUtil.getCurrentUser();
    }
}