package com.financial.api.service;

import com.financial.api.dto.request.AccountCreateRequest;
import com.financial.api.dto.request.AccountUpdateRequest;
import com.financial.api.dto.response.AccountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    Page<AccountResponse> getAll(Pageable pageable);
    AccountResponse createAccount(AccountCreateRequest request);
    AccountResponse updateAccount(Long id, AccountUpdateRequest request);
    AccountResponse getAccountById(Long id);
    void deleteAccount(Long id);
}
