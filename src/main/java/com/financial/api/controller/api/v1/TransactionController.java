package com.financial.api.controller.api.v1;

import com.financial.api.dto.request.TransactionCreateRequest;
import com.financial.api.dto.request.TransactionUpdateRequest;
import com.financial.api.dto.response.TransactionResponse;
import com.financial.api.service.TransactionService;
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
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction Management", description = "APIs for managing transaction (supports both Cookie and Bearer token authentication)")
@SecurityRequirement(name = BEARER_AUTH)
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    @Operation(
            summary = "Get all transactions",
            description = "Retrieve paginated list of all transactions. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<List<TransactionResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> transactions = transactionService.getAll(pageable);
        AppApiResponse<List<TransactionResponse>> response = AppApiResponse.success(
                "Get All Transactions Successfully",
                transactions.getContent(),
                new ApiPaginationMetadata(
                        transactions.getNumber(),
                        transactions.getSize(),
                        transactions.getTotalPages(),
                        transactions.getTotalElements()
                )
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
            summary = "Create a new transaction",
            description = "Create a new transaction in the system. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionCreateRequest request
    ) {
        TransactionResponse createdTransaction = transactionService.createTransaction(request);
        AppApiResponse<TransactionResponse> response = AppApiResponse.success(
                "Transaction created successfully",
                createdTransaction
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieve transaction details by ID. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<TransactionResponse>> getTransactionById(@PathVariable Long id) {
        TransactionResponse transaction = transactionService.getTransactionById(id);
        AppApiResponse<TransactionResponse> response = AppApiResponse.success(transaction);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update transaction",
            description = "Update an existing transaction. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<TransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateRequest request) {

        TransactionResponse transaction = transactionService.updateTransaction(id, request);
        return ResponseEntity.ok(AppApiResponse.success("Transaction updated successfully", transaction));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete transaction",
            description = "Delete a transaction by ID. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(AppApiResponse.success("Transaction deleted successfully", null));
    }
}
