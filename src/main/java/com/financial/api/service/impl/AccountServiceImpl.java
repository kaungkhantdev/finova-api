package com.financial.api.service.impl;

import com.financial.api.dto.mapper.AccountMapper;
import com.financial.api.dto.request.AccountCreateRequest;
import com.financial.api.dto.request.AccountUpdateRequest;
import com.financial.api.dto.response.AccountResponse;
import com.financial.api.dto.response.TransactionResponse;
import com.financial.api.entity.*;
import com.financial.api.repository.AccountRepository;
import com.financial.api.repository.CategoryRepository;
import com.financial.api.repository.CurrencyRepository;
import com.financial.api.service.AccountService;
import com.financial.api.util.AuthenticationUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CurrencyRepository currencyRepository;
    private final CategoryRepository categoryRepository;
    private final AccountMapper accountMapper;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public Page<AccountResponse> getAll(Pageable pageable) {
        User currentUser = getCurrentUser();

        Page<Account> transactions = accountRepository.findByUserAndIsDeletedFalse(currentUser, pageable);
        return transactions.map(accountMapper::toResponse);
    }

    @Override
    public AccountResponse createAccount(AccountCreateRequest request) {
        User currentUser = getCurrentUser();

        if (accountRepository.existsByUserAndNameIgnoreCaseAndIsDeletedFalse(currentUser, request.getName())) {
            throw new IllegalArgumentException("You already have an account with name: " + request.getName());
        }

        Category category = getCategory(request.getCategoryId());
        Currency currency = getCurrency(request.getCurrencyId());

        Account account = accountMapper.toEntity(request, currentUser, category, currency);
        Account savedAccount = accountRepository.save(account);

        return accountMapper.toResponse(savedAccount);
    }

    @Override
    public AccountResponse updateAccount(Long id, AccountUpdateRequest request) {
        User currentUser = getCurrentUser();

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + id));

        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("You do not have permission to update this account.");
        }

        if (request.getName() != null &&
                !request.getName().equalsIgnoreCase(account.getName()) &&
                accountRepository.existsByUserAndNameIgnoreCaseAndIsDeletedFalse(currentUser, request.getName())) {
            throw new IllegalArgumentException("You already have an account with name: " + request.getName());
        }

        Category category = request.getCategoryId() != null ? getCategory(request.getCategoryId()) : null;
        Currency currency = request.getCurrencyId() != null ? getCurrency(request.getCurrencyId()) : null;

        accountMapper.updateEntity(request, account, category, currency);

        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toResponse(updatedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {
        User currentUser = getCurrentUser();

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + id));

        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("You do not have permission to view this account.");
        }

        return accountMapper.toResponse(account);
    }

    @Override
    public void deleteAccount(Long id) {
        User currentUser = getCurrentUser();

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + id));

        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("You do not have permission to delete this account.");
        }

        account.setIsDeleted(true);
        accountRepository.save(account);
    }

    /**
     * Get the currently authenticated user
     */
    private User getCurrentUser() {
        return authenticationUtil.getCurrentUser();
    }

    /**
     * Get the currency with id
     */
    private Currency getCurrency(Long currencyId) {
        return currencyRepository.findById(currencyId)
                .orElseThrow(() -> new EntityNotFoundException("Currency not found with ID: " + currencyId));
    }

    /**
     * Get the category with id
     */
    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + categoryId));
    }
}
