package com.financial.api.service.strategy;

import com.financial.api.entity.Account;

import java.math.BigDecimal;

public interface TransactionBalanceStrategy {
    void process(Account account, BigDecimal amount);
    void revert(Account account, BigDecimal amount);
}
