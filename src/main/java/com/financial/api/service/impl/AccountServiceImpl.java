package com.financial.api.service.impl;

import com.financial.api.dto.mapper.AccountMapper;
import com.financial.api.dto.request.AccountCreateRequest;
import com.financial.api.dto.request.AccountUpdateRequest;
import com.financial.api.dto.response.AccountResponse;
import com.financial.api.dto.response.BalanceResponse;
import com.financial.api.dto.response.MultiCurrencyConversionResponse;
import com.financial.api.entity.*;
import com.financial.api.repository.AccountRepository;
import com.financial.api.repository.CurrencyRepository;
import com.financial.api.service.AccountService;
import com.financial.api.service.ExternalApiService;
import com.financial.api.util.AuthenticationUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CurrencyRepository currencyRepository;
    private final AccountMapper accountMapper;
    private final AuthenticationUtil authenticationUtil;
    private final ExternalApiService externalApiService;

    @Override
    public Page<AccountResponse> getAll(Pageable pageable) {
        User currentUser = getCurrentUser();

        Page<Account> accounts = accountRepository.findByUserAndIsDeletedFalse(currentUser, pageable);
        return accounts.map(accountMapper::toResponse);
    }

    @Override
    public List<AccountResponse> getAllNoPagination() {
        User currentUser = getCurrentUser();

        List<Account> accounts = accountRepository.findAllByUserAndIsDeletedFalse(currentUser);
        return accounts.stream().map(accountMapper::toResponse).toList();
    }

    @Override
    public AccountResponse createAccount(AccountCreateRequest request) {
        User currentUser = getCurrentUser();

        if (accountRepository.existsByUserAndNameIgnoreCaseAndIsDeletedFalse(currentUser, request.getName())) {
            throw new IllegalArgumentException("You already have an account with name: " + request.getName());
        }

        Currency currency = getCurrency(currentUser.getCurrency().getId());

        Account account = accountMapper.toEntity(request, currentUser, currency);
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


        accountMapper.updateEntity(request, account);

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

    @Override
    public BalanceResponse getBalance() {
        User currentUser = getCurrentUser();
        BigDecimal balance = accountRepository.getCurrentUserBalance(currentUser);

        MultiCurrencyConversionResponse convertedData = externalApiService.convertToMultipleCurrencies(currentUser.getCurrency().getCode(), balance);
        BalanceResponse response = new BalanceResponse(convertedData);
        response.setFromCurrencySymbol(currentUser.getCurrency().getSymbol());
        return new BalanceResponse(convertedData);
    }

    /**
     * Get the currently authenticated user
     */
    private User getCurrentUser() {
        return authenticationUtil.getCurrentUserWithCurrency();
    }

    /**
     * Get the currency with id
     */
    private Currency getCurrency(Long currencyId) {
        return currencyRepository.findById(currencyId)
                .orElseThrow(() -> new EntityNotFoundException("Currency not found with ID: " + currencyId));
    }

}
