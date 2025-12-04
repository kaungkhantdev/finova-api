package com.finova.api.service.strategy;

import com.finova.api.entity.Account;
import com.finova.api.exception.InsufficientBalanceException;
import com.finova.api.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.finova.api.constant.TransactionConstants.INCOME_TYPE;

@Slf4j
@Component(INCOME_TYPE)
@RequiredArgsConstructor
public class IncomeBalanceStrategy implements TransactionBalanceStrategy {

    private final AccountRepository accountRepository;

    @Override
    public void process(Account account, BigDecimal amount) {
        creditToAccount(account, amount);
        log.debug("Processed INCOME: credited {} to account {}", amount, account.getId());
    }

    @Override
    public void revert(Account account, BigDecimal amount) {
        validateSufficientBalance(account, amount);
        deductFromAccount(account, amount);
        log.debug("Reverted INCOME: deducted {} from account {}", amount, account.getId());
    }

    private void validateSufficientBalance(Account account, BigDecimal amount) {
        if (account.getAmount().compareTo(amount) < 0) {
            log.error("Insufficient balance - Account: {}, Available: {}, Required: {}",
                    account.getId(), account.getAmount(), amount);
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance in account %d. Available: %s, Required: %s",
                            account.getId(), account.getAmount(), amount)
            );
        }
    }

    private void deductFromAccount(Account account, BigDecimal amount) {
        BigDecimal newBalance = account.getAmount().subtract(amount);
        account.setAmount(newBalance);
        accountRepository.save(account);
        log.debug("Deducted {} from account {}. New balance: {}", amount, account.getId(), newBalance);
    }

    private void creditToAccount(Account account, BigDecimal amount) {
        BigDecimal newBalance = account.getAmount().add(amount);
        account.setAmount(newBalance);
        accountRepository.save(account);
        log.debug("Credited {} to account {}. New balance: {}", amount, account.getId(), newBalance);
    }
}