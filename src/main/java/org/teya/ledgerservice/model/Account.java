package org.teya.ledgerservice.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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
    @Setter
    @Getter
    private boolean isSessionOn = false;
    private final Stack<Transaction> sessionStack = new Stack<>();

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

    public void addTransactionToSession(Transaction transaction){
        sessionStack.push(transaction);
    }

    public boolean commitSession(){
        this.isSessionOn = false;
        return true;
    }

    public void rollbackSession(){
        while(!sessionStack.isEmpty() && !transactions.isEmpty()){
            var transaction = sessionStack.pop();
            if(transaction.getId().equals(transactions.peek().getId())) {
                var toRevert = transactions.removeFirst();
                if(toRevert.getTransactionType() == TransactionType.DEBIT){
                    addBalance(toRevert.getAmount());
                } else {
                    subtractBalance(toRevert.getAmount());
                }
            }
        }
    }
    
    public List<Transaction> getTransactions(int limit){
        if(limit <= 0) return List.of();
        int toIndex = Math.min(limit, transactions.size());
        return transactions.subList(0, toIndex);
    }
}
