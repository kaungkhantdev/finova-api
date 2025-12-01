package com.financial.api.service;

import com.financial.api.dto.request.AccountCreateRequest;
import com.financial.api.dto.request.AccountUpdateRequest;
import com.financial.api.dto.response.AccountResponse;
import com.financial.api.dto.response.AccountWithTotalsResponse;
import com.financial.api.dto.response.BalanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    Page<AccountWithTotalsResponse> getAll(Pageable pageable);
    List<AccountResponse> getAllNoPagination();
    AccountResponse createAccount(AccountCreateRequest request);
    AccountResponse updateAccount(Long id, AccountUpdateRequest request);
    AccountResponse getAccountById(Long id);
    void deleteAccount(Long id);

    BalanceResponse getBalance();
}
