package com.financial.api.service;

import com.financial.api.dto.request.TransactionCreateRequest;
import com.financial.api.dto.request.TransactionUpdateRequest;
import com.financial.api.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    Page<TransactionResponse> getAll(Pageable pageable);
    TransactionResponse createTransaction(TransactionCreateRequest request);
    TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request);
    TransactionResponse getTransactionById(Long id);
    void deleteTransaction(Long id);

    List<TransactionByDateResponse> getTransactionByDate();
    List<TransactionByMonthResponse> getTransactionByMonth();
    List<TransactionByCategoryResponse> getTransactionByCategory(Long transactionTypeId);

    DailyAmountResponse getDailyAmount(Long transactionTypeId);
    WeeklyAmountResponse getWeeklyAmount(Long transactionTypeId);
    MonthlyAmountResponse getMonthlyAmount(Long transactionTypeId);

    MonthlyComparisonResponse getMonthlyComparison();

}
