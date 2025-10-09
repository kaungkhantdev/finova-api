package com.financial.api.service.impl;

import com.financial.api.dto.mapper.CurrencyMapper;
import com.financial.api.dto.request.CurrencyCreateRequest;
import com.financial.api.dto.request.CurrencyUpdateRequest;
import com.financial.api.dto.response.CurrencyResponse;
import com.financial.api.entity.Currency;
import com.financial.api.entity.User;
import com.financial.api.repository.CurrencyRepository;
import com.financial.api.service.CurrencyService;
import com.financial.api.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final AuthenticationUtil authenticationUtil;

    @Override
    @Transactional(readOnly = true)
    public Page<CurrencyResponse> getAll(Pageable pageable) {
        User currentUser = getCurrentUser();

        // Get system currencies + user's own currencies
        Page<Currency> currencies = currencyRepository
                .findByUserOrIsSystemTrueAndIsDeletedFalse(currentUser, pageable);

        return currencies.map(currencyMapper::toResponse);
    }

    @Override
    public CurrencyResponse createCurrency(CurrencyCreateRequest request) {
        User currentUser = getCurrentUser();

        // Check if user already has a currency with this code
        if (currencyRepository.existsByUserAndCodeAndIsDeletedFalse(currentUser, request.getCode())) {
            throw new IllegalArgumentException("You already have a currency with code: " + request.getCode());
        }

        Currency currency = currencyMapper.toEntity(request);
        currency.setUser(currentUser);
        currency.setIsSystem(false); // User-created currencies are not system currencies

        Currency savedCurrency = currencyRepository.save(currency);
        return currencyMapper.toResponse(savedCurrency);
    }

    @Override
    public CurrencyResponse updateCurrency(Long id, CurrencyUpdateRequest request) {
        User currentUser = getCurrentUser();

        Currency existing = currencyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Currency not found with ID: " + id));

        // Validate user can update this currency
        validateUpdatePermission(existing, currentUser);

        // Check if the new code conflicts with user's other currencies
        if (!existing.getCode().equals(request.getCode()) &&
                currencyRepository.existsByUserAndCodeAndIsDeletedFalse(currentUser, request.getCode())) {
            throw new IllegalArgumentException("You already have a currency with code: " + request.getCode());
        }

        // Update fields
        existing.setCurrency(request.getCurrency());
        existing.setCode(request.getCode());
        existing.setSymbol(request.getSymbol());

        Currency updatedCurrency = currencyRepository.save(existing);
        return currencyMapper.toResponse(updatedCurrency);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyResponse getCurrencyById(Long id) {
//        User currentUser = getCurrentUser();

        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Currency not found with ID: " + id));

        // Check if user has access to this currency (system or their own)
//        if (!currency.getIsSystem() &&
//                (currency.getUser() == null || !currency.getUser().getId().equals(currentUser.getId()))) {
//            throw new AccessDeniedException("You don't have access to this currency");
//        }

        if (currency.getIsDeleted()) {
            throw new NoSuchElementException("Currency not found with ID: " + id);
        }

        return currencyMapper.toResponse(currency);
    }

    @Override
    public void deleteCurrency(Long id) {
        User currentUser = getCurrentUser();

        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Currency not found with ID: " + id));

        // Validate user can delete this currency
        validateUpdatePermission(currency, currentUser);

        // Soft delete
        currency.setIsDeleted(true);
        currencyRepository.save(currency);
    }

    /**
     * Get the currently authenticated user
     */
    private User getCurrentUser() {
        return authenticationUtil.getCurrentUser();
    }

    /**
     * Validate if the current user can update the given currency
     */
    private void validateUpdatePermission(Currency currency, User currentUser) {
        // Cannot update system currencies
        if (currency.getIsSystem()) {
            throw new AccessDeniedException("Cannot modify system currencies");
        }

        // Can only update own currencies
        if (currency.getUser() == null || !currency.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only update your own currencies");
        }
    }
}