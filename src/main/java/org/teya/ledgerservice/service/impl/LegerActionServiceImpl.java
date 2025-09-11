package org.teya.ledgerservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.teya.ledgerservice.model.Account;
import org.teya.ledgerservice.model.Transaction;
import org.teya.ledgerservice.service.LegerActionService;
import org.teya.ledgerservice.service.dto.LedgerAction;
import org.teya.ledgerservice.store.AccountStore;
import org.teya.ledgerservice.service.exception.LedgerException; // added

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LegerActionServiceImpl implements LegerActionService {
    private final AccountStore accountStore;
    private static final BigDecimal MAX_BALANCE = new BigDecimal("100000000000000"); // 100 trillion

    @Override
    public Transaction performAction(LedgerAction ledgerAction) {
        var accountOp = accountStore.getAccount(ledgerAction.accountId());
        if(accountOp.isEmpty()){
            throw new LedgerException("Account or user not found");
        }
        var account = accountOp.get();
        if(account.getCurrency() != ledgerAction.currency()){
            throw new LedgerException(String.format("Currency %s is not supported for %s account", ledgerAction.currency(), account.getCurrency()));
        }
        return switch (ledgerAction.transactionType()){
            case DEBIT -> withdrawAmount(account, ledgerAction);
            case CREDIT -> depositAmount(account, ledgerAction);
        };
    }
    
    public Transaction depositAmount(Account account, LedgerAction ledgerAction) {
        var prospective = account.getAccountBalance().add(ledgerAction.amount());
        if (prospective.compareTo(MAX_BALANCE) >= 0) {
            throw new LedgerException("Balance limit reached: cannot reach or exceed 100000000000000");
        }
        var transaction = mapToTransaction(ledgerAction);
        account.addTransaction(transaction);
        account.addBalance(transaction.getAmount());
        if(account.isSessionOn()){
            account.addTransactionToSession(transaction);
        }
        return transaction;
    }

    private Transaction mapToTransaction(LedgerAction ledgerAction) {
        return new Transaction(
                UUID.randomUUID().toString(),
                ledgerAction.transactionType(),
                ledgerAction.currency(),
                ledgerAction.amount()
        );
    }

    public Transaction withdrawAmount(Account account, LedgerAction ledgerAction) {
        if (account.getAccountBalance().compareTo(ledgerAction.amount()) < 0) {
            throw new LedgerException("Insufficient funds");
        }
        var transaction = mapToTransaction(ledgerAction);
        account.addTransaction(transaction);
        account.subtractBalance(transaction.getAmount());
        if(account.isSessionOn()){
            account.addTransactionToSession(transaction);
        }
        return transaction;
    }
    
}
