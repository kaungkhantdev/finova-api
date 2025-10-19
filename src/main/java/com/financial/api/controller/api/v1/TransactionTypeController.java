package com.financial.api.controller.api.v1;

import com.financial.api.dto.request.TransactionTypeCreateRequest;
import com.financial.api.dto.request.TransactionTypeUpdateRequest;
import com.financial.api.dto.response.TransactionTypeResponse;
import com.financial.api.service.TransactionTypeService;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction-types")
@Tag(name = "TransactionType Management", description = "APIs for managing transaction type (supports both Cookie and Bearer token authentication)")
@SecurityRequirement(name = BEARER_AUTH)
public class TransactionTypeController {

    private final TransactionTypeService transactionTypeService;

    @GetMapping
    @Operation(
            summary = "Get all transactionTypes",
            description = "Retrieve paginated list of all transactionTypes. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<List<TransactionTypeResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionTypeResponse> transactionTypes = transactionTypeService.getAll(pageable);
        AppApiResponse<List<TransactionTypeResponse>> response = AppApiResponse.success(
                "Get All Transaction Types Successfully",
                transactionTypes.getContent(),
                new ApiPaginationMetadata(
                        transactionTypes.getNumber(),
                        transactionTypes.getSize(),
                        transactionTypes.getTotalPages(),
                        transactionTypes.getTotalElements()
                )
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
            summary = "Create a new category",
            description = "Create a new category in the system. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<TransactionTypeResponse>> createTransactionType(
            @Valid @RequestBody TransactionTypeCreateRequest request
    ) {
        TransactionTypeResponse createdTransactionType = transactionTypeService.createTransactionType(request);
        AppApiResponse<TransactionTypeResponse> response = AppApiResponse.success(
                "TransactionType created successfully",
                createdTransactionType
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get category by ID",
            description = "Retrieve category details by ID. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<TransactionTypeResponse>> getTransactionTypeById(@PathVariable Long id) {
        TransactionTypeResponse category = transactionTypeService.getTransactionTypeById(id);
        AppApiResponse<TransactionTypeResponse> response = AppApiResponse.success(category);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update category",
            description = "Update an existing category. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<TransactionTypeResponse>> updateTransactionType(
            @PathVariable Long id,
            @Valid @RequestBody TransactionTypeUpdateRequest request) {

        TransactionTypeResponse category = transactionTypeService.updateTransactionType(id, request);
        return ResponseEntity.ok(AppApiResponse.success("TransactionType updated successfully", category));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete category",
            description = "Delete a category by ID. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<Void>> deleteTransactionType(@PathVariable Long id) {
        transactionTypeService.deleteTransactionType(id);
        return ResponseEntity.ok(AppApiResponse.success("TransactionType deleted successfully", null));
    }
}
