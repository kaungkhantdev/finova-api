package com.financial.api.service.impl;

import com.financial.api.dto.mapper.AccountMapper;
import com.financial.api.dto.request.AccountCreateRequest;
import com.financial.api.dto.request.AccountUpdateRequest;
import com.financial.api.dto.response.AccountResponse;
import com.financial.api.dto.response.AccountWithTotalsResponse;
import com.financial.api.dto.response.BalanceResponse;
import com.financial.api.dto.response.MultiCurrencyConversionResponse;
import com.financial.api.entity.*;
import com.financial.api.repository.AccountRepository;
import com.financial.api.repository.CurrencyRepository;
import com.financial.api.repository.projection.AccountWithTotalsProjection;
import com.financial.api.service.AccountService;
import com.financial.api.service.ExternalApiService;
import com.financial.api.util.AuthenticationUtil;
import com.financial.api.util.NumberFormatter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Page<AccountWithTotalsResponse> getAll(Pageable pageable) {
        User currentUser = getCurrentUser();

        Page<AccountWithTotalsProjection> accounts = accountRepository.findByUserAndIsDeletedFalse(currentUser.getId(), pageable);
        return accounts.map(this::mapToResponse);
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
        response.setFormattedOriginalAmount(NumberFormatter.format(balance));
        response.setFormattedConversions(formattedConversions(convertedData.getConversions()));
        return response;
    }

    private AccountWithTotalsResponse mapToResponse(AccountWithTotalsProjection projection) {
        return AccountWithTotalsResponse.builder()
                .accountId(projection.getAccountId())
                .accountName(projection.getAccountName())
                .description(projection.getDescription())
                .amount(projection.getAmount())
                .currency(projection.getCurrency())
                .currencyCode(projection.getCurrencyCode())
                .currencySymbol(projection.getCurrencySymbol())
                .totalIncome(projection.getTotalIncome())
                .totalExpense(projection.getTotalExpense())
                .build();
    }

    /**
     * Put formatted conversations
     */
    private Map<String, String> formattedConversions(Map<String, BigDecimal> conversions) {
        Map<String, String> formatted = new HashMap<>();

        conversions.forEach((currency, amount) -> {
            formatted.put(currency, NumberFormatter.format(amount));
        });
        return formatted;
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
