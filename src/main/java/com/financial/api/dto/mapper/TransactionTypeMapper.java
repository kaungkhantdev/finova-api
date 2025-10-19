package com.financial.api.dto.mapper;

import com.financial.api.dto.request.TransactionTypeCreateRequest;
import com.financial.api.dto.request.TransactionTypeUpdateRequest;
import com.financial.api.dto.response.CategoryResponse;
import com.financial.api.dto.response.TransactionTypeResponse;
import com.financial.api.entity.TransactionType;
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
