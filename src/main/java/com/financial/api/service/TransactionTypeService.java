package com.financial.api.service;

import com.financial.api.dto.request.TransactionTypeCreateRequest;
import com.financial.api.dto.request.TransactionTypeUpdateRequest;
import com.financial.api.dto.response.TransactionTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionTypeService {
    Page<TransactionTypeResponse> getAll(Pageable pageable);
    TransactionTypeResponse createTransactionType(TransactionTypeCreateRequest request);
    TransactionTypeResponse updateTransactionType(Long id, TransactionTypeUpdateRequest request);
    TransactionTypeResponse getTransactionTypeById(Long id);
    void deleteTransactionType(Long id);
}
