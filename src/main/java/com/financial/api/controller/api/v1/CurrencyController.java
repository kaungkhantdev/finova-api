package com.financial.api.controller.api.v1;

import com.financial.api.dto.request.CurrencyCreateRequest;
import com.financial.api.dto.response.CurrencyResponse;
import com.financial.api.service.CurrencyService;
import com.financial.api.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @PostMapping
    public ResponseEntity<ApiResponse<CurrencyResponse>> createCurrency(@Valid @RequestBody CurrencyCreateRequest request) {
        CurrencyResponse createdCurrency = currencyService.createCurrency(request);
        ApiResponse<CurrencyResponse> response = ApiResponse.success(
                "Currency created successfully",
                createdCurrency
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CurrencyResponse>> getUserById(@PathVariable Long id) {
        CurrencyResponse currency = currencyService.getCurrencyById(id);
        ApiResponse<CurrencyResponse> response = ApiResponse.success(currency);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
