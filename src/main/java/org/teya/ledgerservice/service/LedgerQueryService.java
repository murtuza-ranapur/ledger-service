package org.teya.ledgerservice.service;

import org.teya.ledgerservice.model.Account;
import org.teya.ledgerservice.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface LedgerQueryService {
    BigDecimal checkBalance(String accountId);

    List<Transaction> getTransactions(String accountId, int limit);

    List<Account> getAccounts();
}
