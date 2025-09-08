package org.teya.ledgerservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.teya.ledgerservice.model.Account;
import org.teya.ledgerservice.model.Transaction;
import org.teya.ledgerservice.service.LedgerQueryService;
import org.teya.ledgerservice.store.AccountStore;
import org.teya.ledgerservice.service.exception.LedgerException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LegerQueryServiceImpl implements LedgerQueryService {
    private final AccountStore accountStore;

    @Override
    public BigDecimal checkBalance(String accountId) {
        var accountOp = accountStore.getAccount(accountId);
        if(accountOp.isEmpty()){
            throw new LedgerException("Account or user not found");
        }
        return accountOp.get().getAccountBalance();
    }

    @Override
    public List<Transaction> getTransactions(String accountId, int limit) {
        var accountOp = accountStore.getAccount(accountId);
        if(accountOp.isEmpty()){
            throw new LedgerException("Account or user not found");
        }
        return accountOp.get().getTransactions(limit);
    }

    @Override
    public List<Account> getAccounts() {
        return accountStore.getAllAccounts();
    }
}
