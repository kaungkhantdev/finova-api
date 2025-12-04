package com.finova.api.dto.mapper;

import com.finova.api.dto.request.CurrencyCreateRequest;
import com.finova.api.dto.request.CurrencyUpdateRequest;
import com.finova.api.dto.response.CurrencyResponse;
import com.finova.api.entity.Currency;
import org.springframework.stereotype.Component;


@Component()
public class CurrencyMapper {
    public Currency toEntity(CurrencyCreateRequest request) {
        Currency currency = new Currency();
        currency.setCurrency(request.getCurrency());    
        currency.setSymbol(request.getSymbol());
        currency.setCode(request.getCode());
        return currency;
    }

    public CurrencyResponse toResponse(Currency currency) {
        CurrencyResponse response = new CurrencyResponse();
        response.setId(currency.getId());
        response.setCurrency(currency.getCurrency());
        response.setCode(currency.getCode());
        response.setSymbol(currency.getSymbol());
        return response;
    }

    public void updateEntity(Currency currency, CurrencyUpdateRequest request) {
        if (request.getCurrency() != null) {
            currency.setCurrency(request.getCurrency());
        }
        if (request.getCode() != null) {
            currency.setCode(request.getCode());
        }
        if (request.getSymbol() != null) {
            currency.setSymbol(request.getSymbol());
        }
    }
}
