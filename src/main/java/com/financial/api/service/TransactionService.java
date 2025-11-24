package com.financial.api.service;

import com.financial.api.dto.request.TransactionCreateRequest;
import com.financial.api.dto.request.TransactionUpdateRequest;
import com.financial.api.dto.response.TransactionByDateResponse;
import com.financial.api.dto.response.TransactionByMonthResponse;
import com.financial.api.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    Page<TransactionResponse> getAll(Pageable pageable);
    TransactionResponse createTransaction(TransactionCreateRequest request);
    TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request);
    TransactionResponse getTransactionById(Long id);
    void deleteTransaction(Long id);

    TransactionByDateResponse getTransactionByDate();
    TransactionByMonthResponse getTransactionByMonth();

}
