package org.teya.ledgerservice.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Account{
    @Getter
    private final String id;
    @Getter
    private final String number;
    @Getter
    private final String sortCode;
    @Getter
    private final String userId;
    @Getter
    private final Currency currency;
    private final AccountBalance accountBalance;
    private final LinkedList<Transaction> transactions;

    private Account(
            String id,
            String number,
            String sortCode,
            String userId,
            Currency currency,
            AccountBalance balance){
        this.id = id;
        this.number = number;
        this.sortCode = sortCode;
        this.userId = userId;
        this.currency = currency;
        this.accountBalance = balance;
        this.transactions = new LinkedList<>();
    }

    public static Account createAccount(String id, String number, String sortCode, String userId, Currency currency) {
        return new Account(id, number, sortCode, userId, currency, new AccountBalance());
    }

    public BigDecimal getAccountBalance(){
        return accountBalance.getCurrentBalance();
    }

    public void addBalance(BigDecimal amount){
        accountBalance.setCurrentBalance(accountBalance.getCurrentBalance().add(amount));
    }

    public void subtractBalance(BigDecimal amount){
        accountBalance.setCurrentBalance(accountBalance.getCurrentBalance().subtract(amount));
    }

    public void addTransaction(Transaction transaction){
        // store newest first
        this.transactions.addFirst(transaction);
    }
    
    public List<Transaction> getTransactions(int limit){
        if(limit <= 0) return List.of();
        int toIndex = Math.min(limit, transactions.size());
        return transactions.subList(0, toIndex);
    }
}
