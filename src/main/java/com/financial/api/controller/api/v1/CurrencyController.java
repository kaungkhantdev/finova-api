package com.financial.api.controller.api.v1;

import com.financial.api.dto.request.CurrencyCreateRequest;
import com.financial.api.dto.request.CurrencyUpdateRequest;
import com.financial.api.dto.response.CurrencyResponse;
import com.financial.api.service.CurrencyService;
import com.financial.api.util.ApiPaginationMetadata;
import com.financial.api.util.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.financial.api.config.OpenApiConfig.BEARER_AUTH;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
@Tag(name = "Currency Management", description = "APIs for managing currencies (supports both Cookie and Bearer token authentication)")
@SecurityRequirement(name = BEARER_AUTH)
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    @Operation(
            summary = "Get all currencies",
            description = "Retrieve paginated list of all currencies. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<List<CurrencyResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CurrencyResponse> currencies = currencyService.getAll(pageable);
        AppApiResponse<List<CurrencyResponse>> response = AppApiResponse.success(
                "Get all currencies successfully",
                currencies.getContent(),
                new ApiPaginationMetadata(
                        currencies.getNumber(),
                        currencies.getSize(),
                        currencies.getTotalPages(),
                        currencies.getTotalElements())
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
            summary = "Create a new currency",
            description = "Create a new currency in the system. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<CurrencyResponse>> createCurrency(
            @Valid @RequestBody CurrencyCreateRequest request) {
        CurrencyResponse createdCurrency = currencyService.createCurrency(request);
        AppApiResponse<CurrencyResponse> response = AppApiResponse.success(
                "Currency created successfully",
                createdCurrency
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get currency by ID",
            description = "Retrieve currency details by ID. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<CurrencyResponse>> getCurrencyById(@PathVariable Long id) {
        CurrencyResponse currency = currencyService.getCurrencyById(id);
        AppApiResponse<CurrencyResponse> response = AppApiResponse.success(currency);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update currency",
            description = "Update an existing currency. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<CurrencyResponse>> updateCurrency(
            @PathVariable Long id,
            @Valid @RequestBody CurrencyUpdateRequest request) {

        CurrencyResponse currency = currencyService.updateCurrency(id, request);
        return ResponseEntity.ok(AppApiResponse.success("Currency updated successfully", currency));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete currency",
            description = "Delete a currency by ID. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<Void>> deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrency(id);
        return ResponseEntity.ok(AppApiResponse.success("Currency deleted successfully", null));
    }
}