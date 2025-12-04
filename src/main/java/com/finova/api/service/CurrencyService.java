package com.finova.api.service;

import com.finova.api.dto.request.CurrencyCreateRequest;
import com.finova.api.dto.request.CurrencyUpdateRequest;
import com.finova.api.dto.response.CurrencyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CurrencyService {
    Page<CurrencyResponse> getAll(Pageable pageable);
    List<CurrencyResponse> getAllNoPagination();
    CurrencyResponse createCurrency(CurrencyCreateRequest request);
    CurrencyResponse updateCurrency(Long id, CurrencyUpdateRequest request);
    CurrencyResponse getCurrencyById(Long id);
    void deleteCurrency(Long id);
}

