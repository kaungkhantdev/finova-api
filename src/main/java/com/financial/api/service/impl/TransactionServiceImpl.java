package com.financial.api.service.impl;

import com.financial.api.dto.mapper.TransactionMapper;
import com.financial.api.dto.request.TransactionCreateRequest;
import com.financial.api.dto.request.TransactionUpdateRequest;
import com.financial.api.dto.response.*;
import com.financial.api.entity.*;
import com.financial.api.exception.InsufficientBalanceException;
import com.financial.api.repository.AccountRepository;
import com.financial.api.repository.CategoryRepository;
import com.financial.api.repository.TransactionRepository;
import com.financial.api.repository.TransactionTypeRepository;
import com.financial.api.repository.projection.DailyAmountProjection;
import com.financial.api.repository.projection.MonthlyAmountProjection;
import com.financial.api.repository.projection.MonthlyComparisonProjection;
import com.financial.api.repository.projection.WeeklyAmountProjection;
import com.financial.api.service.TransactionService;
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

import static com.financial.api.constant.TransactionConstants.EXPENSE_TYPE;
import static com.financial.api.constant.TransactionConstants.INCOME_TYPE;

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
    public List<TransactionByDateResponse> getTransactionByDate() {
        User currentUser = getCurrentUser();
        return transactionRepository.getTransactionByDate(currentUser.getId())
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
    public List<TransactionByCategoryResponse> getTransactionByCategory(Long transactionTypeId) {
        User currentUser = getCurrentUser();
        return transactionRepository.getTransactionsByCategory(currentUser.getId(), transactionTypeId)
                .stream()
                .map(TransactionByCategoryResponse::new)
                .toList();
    }
    @Override
    public DailyAmountResponse getDailyAmount(Long transactionTypeId) {
        User currentUser = getCurrentUser();
        DailyAmountProjection data = transactionRepository.getDailyAmount(currentUser.getId(), transactionTypeId);
        return mapToResponse(data);
    }


    @Override
    public WeeklyAmountResponse getWeeklyAmount(Long transactionTypeId) {
        User currentUser = getCurrentUser();
        WeeklyAmountProjection data = transactionRepository.getWeeklyAmount(currentUser.getId(), transactionTypeId);
        return mapWeeklyToResponse(data);
    }

    @Override
    public MonthlyAmountResponse getMonthlyAmount(Long transactionTypeId) {
        User currentUser = getCurrentUser();
        MonthlyAmountProjection data = transactionRepository.getMonthlyAmount(currentUser.getId(), transactionTypeId);
        return mapMonthlyToResponse(data);
    }

    @Override
    public MonthlyComparisonResponse getMonthlyComparison() {
        User currentUser = getCurrentUser();
        MonthlyComparisonProjection data = transactionRepository.getMonthlyComparison(currentUser.getId());
        return mapMonthlyComparisonToResponse(data);
    }

    // ==================== Balance Processing Methods ====================
    private MonthlyComparisonResponse mapMonthlyComparisonToResponse(MonthlyComparisonProjection data) {
        if (data == null) {
            return new MonthlyComparisonResponse(null, null, null, null, null, null);
        }
        return new MonthlyComparisonResponse(
                data.getCurrentIncome(),
                data.getPreviousIncome(),
                data.getIncomeChangePercent(),
                data.getCurrentExpense(),
                data.getPreviousExpense(),
                data.getExpenseChangePercent()
        );
    }

    private MonthlyAmountResponse mapMonthlyToResponse(MonthlyAmountProjection data) {
        if (data == null) {
            return new MonthlyAmountResponse(null, LocalDate.now().getMonthValue());
        }
        return new MonthlyAmountResponse(data.getMonthlyAmount(), data.getMonth());
    }

    private WeeklyAmountResponse mapWeeklyToResponse(WeeklyAmountProjection data) {
        if (data == null) {
            return new WeeklyAmountResponse( null, LocalDate.now(), LocalDate.now());
        }
        return new WeeklyAmountResponse(data.getWeeklyAmount(), data.getWeekStart(), data.getWeekEnd());
    }

    private DailyAmountResponse mapToResponse(DailyAmountProjection data) {
        if (data == null) {
            return new DailyAmountResponse(null, LocalDate.now());
        }
        return new DailyAmountResponse(data.getDailyAmount(), data.getDate());
    }

    private void processAccountBalance(Account account, TransactionType transactionType, BigDecimal amount) {
        String typeName = transactionType.getName();

        switch (typeName) {
            case EXPENSE_TYPE:
                validateSufficientBalance(account, amount);
                deductFromAccount(account, amount);
                break;
            case INCOME_TYPE:
                creditToAccount(account, amount);
                break;
            default:
                log.warn("Unknown transaction type: {}", typeName);
        }
    }

    private void revertAccountBalance(Account account, TransactionType transactionType, BigDecimal amount) {
        String typeName = transactionType.getName();

        switch (typeName) {
            case EXPENSE_TYPE:
                // Reverting expense = add money back
                creditToAccount(account, amount);
                log.debug("Reverted EXPENSE: added {} back to account {}", amount, account.getId());
                break;
            case INCOME_TYPE:
                // Reverting income = remove money (must have sufficient balance)
                validateSufficientBalance(account, amount);
                deductFromAccount(account, amount);
                log.debug("Reverted INCOME: deducted {} from account {}", amount, account.getId());
                break;
            default:
                log.warn("Unknown transaction type for revert: {}", typeName);
        }
    }

    private void validateSufficientBalance(Account account, BigDecimal amount) {
        if (account.getAmount().compareTo(amount) < 0) {
            log.error("Insufficient balance - Account: {}, Available: {}, Required: {}",
                    account.getId(), account.getAmount(), amount);
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance in account %d. Available: %s, Required: %s",
                            account.getId(), account.getAmount(), amount)
            );
        }
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid amount: {}", amount);
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }
    }

    private void deductFromAccount(Account account, BigDecimal amount) {
        BigDecimal newBalance = account.getAmount().subtract(amount);
        account.setAmount(newBalance);
        accountRepository.save(account);
        log.debug("Deducted {} from account {}. New balance: {}", amount, account.getId(), newBalance);
    }

    private void creditToAccount(Account account, BigDecimal amount) {
        BigDecimal newBalance = account.getAmount().add(amount);
        account.setAmount(newBalance);
        accountRepository.save(account);
        log.debug("Credited {} to account {}. New balance: {}", amount, account.getId(), newBalance);
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