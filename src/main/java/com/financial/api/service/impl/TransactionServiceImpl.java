package com.financial.api.service.impl;

import com.financial.api.dto.mapper.TransactionMapper;
import com.financial.api.dto.request.TransactionCreateRequest;
import com.financial.api.dto.request.TransactionUpdateRequest;
import com.financial.api.dto.response.TransactionResponse;
import com.financial.api.entity.*;
import com.financial.api.repository.AccountRepository;
import com.financial.api.repository.CategoryRepository;
import com.financial.api.repository.TransactionRepository;
import com.financial.api.repository.TransactionTypeRepository;
import com.financial.api.service.TransactionService;
import com.financial.api.util.AuthenticationUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public Page<TransactionResponse> getAll(Pageable pageable) {
        User currentUser = getCurrentUser();

        // Get system + user's own
        Page<Transaction> transactions = transactionRepository.findAll(pageable);

        return transactions.map(transactionMapper::toResponse);
    }

    @Override
    public TransactionResponse createTransaction(TransactionCreateRequest request) {
        User currentUser =  getCurrentUser();
        Category category = getCategory(request.getCategoryId());
        Account account = getAccount(request.getAccountId());
        TransactionType transactionType = getTransactionType(request.getTransactionTypeId());


        Transaction transaction = transactionMapper.toEntity(request, currentUser, transactionType, account, category);
        Transaction savedCategory = transactionRepository.save(transaction);
        return transactionMapper.toResponse(savedCategory);
    }

    @Override
    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with ID: " + id));

        Category category = request.getCategoryId() != null ? getCategory(request.getCategoryId()) : null;
        Account account = request.getAccountId() != null ? getAccount(request.getAccountId()) : null;
        TransactionType transactionType = request.getTransactionTypeId() != null ? getTransactionType(request.getTransactionTypeId()) : null;

        transactionMapper.updateEntity(request, transaction, transactionType, account, category);

        Transaction updatedAccount = transactionRepository.save(transaction);
        return transactionMapper.toResponse(updatedAccount);
    }

    @Override
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with ID: " + id));
        return transactionMapper.toResponse(transaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with ID: " + id));
        transaction.setIsDeleted(true);
        transactionRepository.save(transaction);
    }

    /**
     * Get the currently authenticated user
     */
    private User getCurrentUser() {
        return authenticationUtil.getCurrentUser();
    }

    /**
     * Get the category with id
     */
    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + categoryId));
    }

    /**
     * Get the account with id
     */
    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + accountId));
    }

    /**
     * Get the transaction type with id
     */
    private TransactionType getTransactionType(Long transactionTypeId) {
        return transactionTypeRepository.findById(transactionTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction Type not found with ID: " + transactionTypeId));
    }

}
