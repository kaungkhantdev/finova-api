package com.financial.api.controller.api.v1;

import com.financial.api.dto.request.TransactionCreateRequest;
import com.financial.api.dto.request.TransactionUpdateRequest;
import com.financial.api.dto.response.*;
import com.financial.api.service.TransactionService;
import com.financial.api.util.ApiPaginationMetadata;
import com.financial.api.util.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String s
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> transactions = transactionService.getAll(pageable, s);
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

    @GetMapping("/get-by-days")
    @Operation(
            summary = "Get transactions grouped by date",
            description = "Retrieve transactions aggregated by date. Returns transaction totals for each date. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<List<TransactionByDateResponse>>> getTransactionByDate(
            @Parameter(description = "Number of days (e.g., 7, 30, 90)", required = true)
            @RequestParam(name = "days", defaultValue = "90") Integer days
    ) {
        return ResponseEntity.ok(
                AppApiResponse.success(
                        "Get Transaction By Date successfully",
                        transactionService.getTransactionByDate(days)
                )
        );
    }

    @GetMapping("/by-month")
    @Operation(
            summary = "Get transactions grouped by month",
            description = "Retrieve transactions aggregated by month. Returns transaction totals for each month. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<List<TransactionByMonthResponse>>> getTransactionByMonth() {
        return ResponseEntity.ok(
                AppApiResponse.success(
                        "Get Transaction By Month successfully",
                        transactionService.getTransactionByMonth()
                )
        );
    }

    @GetMapping("/amount-percentage")
    @Operation(
            summary = "Get transactions",
            description = "Retrieve transactions aggregated by category for a specific transaction type (income/expense). Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<AmountPercentageResponse>> getTransactionByCategory(
            @Parameter(description = "Transaction type ID (e.g., 1 for income, 2 for expense)", required = true)
            @RequestParam(name = "transaction_type_id", defaultValue = "1") Long transactionTypeId
    ) {
        return ResponseEntity.ok(
                AppApiResponse.success(
                        "Get Transaction By Category successfully",
                        transactionService.getAmountPercentage(transactionTypeId)
                )
        );
    }


    @GetMapping("/monthly-comparison")
    @Operation(
            summary = "Get monthly comparison",
            description = "Compare current month's transactions with the previous month. Returns income and expense comparisons with percentage changes. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<MonthlyComparisonResponse>> getMonthlyComparison() {
        return ResponseEntity.ok(
                AppApiResponse.success(
                        "Get Monthly Comparison successfully",
                        transactionService.getMonthlyComparison()
                )
        );
    }
}