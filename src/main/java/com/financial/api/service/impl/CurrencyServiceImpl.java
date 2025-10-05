package com.financial.api.service.impl;

import com.financial.api.dto.mapper.CurrencyMapper;
import com.financial.api.dto.request.CurrencyCreateRequest;
import com.financial.api.dto.request.CurrencyUpdateRequest;
import com.financial.api.dto.response.CurrencyResponse;
import com.financial.api.entity.Currency;
import com.financial.api.repository.CurrencyRepository;
import com.financial.api.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Override
    public Page<CurrencyResponse> getAll(Pageable pageable) {
        return currencyRepository.findAll(pageable).map(currencyMapper::toResponse);
    }

    @Override
    public CurrencyResponse createCurrency(CurrencyCreateRequest request) {

        Currency currency = currencyMapper.toEntity(request);
        Currency savedCurrency = currencyRepository.save(currency);

        return currencyMapper.toResponse(savedCurrency);
    }

    @Override
    public CurrencyResponse updateCurrency(Long id, CurrencyUpdateRequest request) {

        Currency existing = currencyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Currency not found with ID: " + id));

        // Map updated fields (you can also use MapStruct @MappingTarget here)
        existing.setCurrency(request.getCurrency());
        existing.setCode(request.getCode());
        existing.setSymbol(request.getSymbol());

        Currency updatedCurrency = currencyRepository.save(existing);

        return currencyMapper.toResponse(updatedCurrency);
    }

    @Override
    public CurrencyResponse getCurrencyById(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new Error("Currency not found with ID: " + id));

        return currencyMapper.toResponse(currency);
    }
}
