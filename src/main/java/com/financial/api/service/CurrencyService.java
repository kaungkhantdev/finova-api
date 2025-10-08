package com.financial.api.service;

import com.financial.api.dto.request.CurrencyCreateRequest;
import com.financial.api.dto.request.CurrencyUpdateRequest;
import com.financial.api.dto.response.CurrencyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CurrencyService {
    Page<CurrencyResponse> getAll(Pageable pageable);
    CurrencyResponse createCurrency(CurrencyCreateRequest request);
    CurrencyResponse updateCurrency(Long id, CurrencyUpdateRequest request);
    CurrencyResponse getCurrencyById(Long id);
    void deleteCurrency(Long id);
}

