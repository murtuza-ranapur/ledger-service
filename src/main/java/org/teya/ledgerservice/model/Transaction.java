package org.teya.ledgerservice.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Transaction {
    private final String id;
    private final TransactionType transactionType;
    private final Currency currency;
    private final BigDecimal amount;
    private final long transactionTime;

    public Transaction(String id,
                       TransactionType transactionType,
                       Currency currency,
                       BigDecimal amount
    ) {
        this.id = id;
        this.transactionType = transactionType;
        this.currency = currency;
        this.amount = amount;
        this.transactionTime = System.currentTimeMillis();
    }
}
