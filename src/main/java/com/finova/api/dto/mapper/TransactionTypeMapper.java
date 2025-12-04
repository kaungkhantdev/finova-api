package com.finova.api.dto.mapper;

import com.finova.api.dto.request.TransactionTypeCreateRequest;
import com.finova.api.dto.request.TransactionTypeUpdateRequest;
import com.finova.api.dto.response.TransactionTypeResponse;
import com.finova.api.entity.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class TransactionTypeMapper {
    public TransactionType toEntity(TransactionTypeCreateRequest request) {
        TransactionType transactionType = new TransactionType();
        transactionType.setName(request.getName());
        transactionType.setDescription(request.getDescription());
        return transactionType;
    }

    public TransactionTypeResponse toResponse(TransactionType transactionType) {
        TransactionTypeResponse response = new TransactionTypeResponse();
        response.setId(transactionType.getId());
        response.setName(transactionType.getName());
        response.setDescription(transactionType.getDescription());
        response.setCreatedAt(transactionType.getCreatedAt());
        response.setUpdatedAt(transactionType.getUpdatedAt());
        return response;
    }

    public void updateEntity(TransactionType transactionType, TransactionTypeUpdateRequest request) {
        if (request.getName() != null) {
            transactionType.setName(request.getName());
        }
        if (request.getDescription() != null) {
            transactionType.setDescription(request.getDescription());
        }
    }
}
