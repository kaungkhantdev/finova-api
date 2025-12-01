package com.financial.api.controller.api.v1;

import com.financial.api.dto.request.AccountCreateRequest;
import com.financial.api.dto.request.AccountUpdateRequest;
import com.financial.api.dto.response.AccountResponse;
import com.financial.api.dto.response.AccountWithTotalsResponse;
import com.financial.api.dto.response.BalanceResponse;
import com.financial.api.dto.response.MultiCurrencyConversionResponse;
import com.financial.api.service.AccountService;
import com.financial.api.util.ApiPaginationMetadata;
import com.financial.api.util.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing financial accounts.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(
        name = "Account Management",
        description = "Endpoints for creating, updating, retrieving, and deleting user accounts"
)
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    @Operation(
            summary = "Get all transactions",
            description = "Retrieve paginated list of all transactions. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<List<AccountWithTotalsResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountWithTotalsResponse> transactions = accountService.getAll(pageable);
        AppApiResponse<List<AccountWithTotalsResponse>> response = AppApiResponse.success(
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

    @GetMapping("all")
    @Operation(
            summary = "Get all transactions - no pagination",
            description = "Retrieve paginated list of all transactions. Requires authentication via Cookie (web) or Bearer token (mobile)."
    )
    public ResponseEntity<AppApiResponse<List<AccountResponse>>> getAllNoPagination() {
        List<AccountResponse> transactions = accountService.getAllNoPagination();
        AppApiResponse<List<AccountResponse>> response = AppApiResponse.success(transactions);

        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Create a new account",
            description = "Registers a new account for the authenticated user with a specific category and currency."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AppApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody AccountCreateRequest request) {

        AccountResponse createdAccount = accountService.createAccount(request);
        AppApiResponse<AccountResponse> response = AppApiResponse.success(createdAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(
            summary = "Update an existing account",
            description = "Partially updates an account’s details such as name, description, amount, category, or currency."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account updated successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<AppApiResponse<AccountResponse>> updateAccount(
            @Parameter(description = "ID of the account to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AccountUpdateRequest request) {

        AccountResponse updatedAccount = accountService.updateAccount(id, request);
        AppApiResponse<AccountResponse> response = AppApiResponse.success(updatedAccount);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get account details by ID",
            description = "Retrieves a single account belonging to the authenticated user by its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AppApiResponse<AccountResponse>> getAccountById(
            @Parameter(description = "ID of the account to retrieve", example = "1")
            @PathVariable Long id) {

        AccountResponse account = accountService.getAccountById(id);
        AppApiResponse<AccountResponse> response = AppApiResponse.success(account);
        return ResponseEntity.ok(response);
    }
    

//    @Operation(
//            summary = "Delete an account",
//            description = "Performs a soft delete on the specified account (marks it as deleted rather than removing it)."
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
//            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
//    })
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteAccount(
//            @Parameter(description = "ID of the account to delete", example = "1")
//            @PathVariable Long id) {
//
//        accountService.deleteAccount(id);
//        return ResponseEntity.noContent().build();
//    }

    @Operation(
            summary = "Get your balance",
            description = "Retrieves all account balance belonging to the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get your balance",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
    })
    @GetMapping("/get-balance")
    public  ResponseEntity<AppApiResponse<BalanceResponse>> getBalance() {
        BalanceResponse account = accountService.getBalance();
        AppApiResponse<BalanceResponse> response = AppApiResponse.success(account);
        return ResponseEntity.ok(response);
    }
}
