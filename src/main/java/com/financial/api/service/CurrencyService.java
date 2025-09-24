package com.financial.api.service;

import com.financial.api.dto.request.CurrencyCreateRequest;
import com.financial.api.dto.request.CurrencyUpdateRequest;
import com.financial.api.dto.response.CurrencyResponse;

public interface CurrencyService {
    CurrencyResponse createCurrency(CurrencyCreateRequest request);
    CurrencyResponse updateCurrency(Long id, CurrencyUpdateRequest request);
    CurrencyResponse getCurrencyById(Long id);
}
