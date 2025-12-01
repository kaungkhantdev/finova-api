package com.financial.api.dto.mapper;

import com.financial.api.dto.request.AccountCreateRequest;
import com.financial.api.dto.request.AccountUpdateRequest;
import com.financial.api.dto.response.AccountResponse;
import com.financial.api.entity.Account;
import com.financial.api.entity.Currency;
import com.financial.api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(AccountCreateRequest request, User user, Currency currency) {
        if (request == null) {
            return null;
        }

        Account account = new Account();
        account.setName(request.getName());
        account.setDescription(request.getDescription());
        account.setUser(user);
        account.setCurrency(currency);

        return account;
    }

    public void updateEntity(AccountUpdateRequest request, Account account) {
        if (request == null) {
            return;
        }

        account.setName(request.getName());
        account.setDescription(request.getDescription());
    }

    public AccountResponse toResponse(Account account) {
        if (account == null) {
            return null;
        }

        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setName(account.getName());
        response.setDescription(account.getDescription());
        response.setAmount(account.getAmount());
        response.setUserId(account.getUser() != null ? account.getUser().getId() : null);
        response.setCurrencyId(account.getCurrency() != null ? account.getCurrency().getId() : null);
        response.setCurrencyCode(account.getCurrency() != null ? account.getCurrency().getCode() : null);
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());

        return response;
    }
}