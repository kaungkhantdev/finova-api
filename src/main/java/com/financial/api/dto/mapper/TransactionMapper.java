package com.financial.api.dto.mapper;

import com.financial.api.dto.request.TransactionCreateRequest;
import com.financial.api.dto.request.TransactionUpdateRequest;
import com.financial.api.dto.response.TransactionResponse;
import com.financial.api.dto.response.TransactionResponse;
import com.financial.api.entity.*;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public Transaction toEntity(
            TransactionCreateRequest request,
            User user,
            TransactionType transactionType,
            Account account,
            Category category
    ) {
        if (request == null) {
            return null;
        }

        Transaction transaction = new Transaction();
        transaction.setName(request.getName());
        transaction.setDescription(request.getDescription());
        transaction.setUser(user);
        transaction.setTransactionType(transactionType);
        transaction.setAccount(account);
        transaction.setCategory(category);

        return  transaction;
    }

    public void updateEntity(
            TransactionUpdateRequest request,
            Transaction transaction,
            TransactionType transactionType,
            Account account,
            Category category
    ) {

        transaction.setName(transaction.getName());
        transaction.setDescription(transaction.getDescription());
        transaction.setTransactionType(transactionType);
        transaction.setAccount(account);
        transaction.setCategory(category);
    }

    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setName(transaction.getName());
        response.setDescription(transaction.getDescription());
        response.setAmount(transaction.getAmount());
        response.setUserId(transaction.getUser() != null ? transaction.getUser().getId() : null);
        response.setAccountId(transaction.getAccount() != null ? transaction.getAccount().getId() : null);
        response.setAccountName(transaction.getAccount() != null ? transaction.getAccount().getName() : null);
        response.setCategoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null);
        response.setCategoryName(transaction.getCategory() != null ? transaction.getCategory().getName() : null);
        response.setTransactionTypeId(transaction.getTransactionType() != null ? transaction.getTransactionType().getId() : null);
        response.setTransactionTypeName(transaction.getTransactionType() != null ? transaction.getTransactionType().getName() : null);
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());

        return response;
    }
}
