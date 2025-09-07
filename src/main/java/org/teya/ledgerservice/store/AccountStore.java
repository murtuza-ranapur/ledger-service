package org.teya.ledgerservice.store;

import org.springframework.stereotype.Repository;
import org.teya.ledgerservice.model.Account;
import org.teya.ledgerservice.model.Currency;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AccountStore {
    private final Map<String, Account> accountMap;

    public AccountStore() {
        this.accountMap = new HashMap<>();
        //Add some sample accounts
        this.accountMap.put(
                "test-account-1",
                Account.createAccount("test-account-1", "11333444", "150061", "user-id-1", Currency.GBP)
        );

        this.accountMap.put(
                "test-account-2",
                Account.createAccount("test-account-2","22333444", "110011", "user-id-2", Currency.GBP)
        );
    }

    public Optional<Account> getAccount(String accountId){
        return Optional.ofNullable(accountMap.get(accountId));
    }

    public List<Account> getAllAccounts(){
        return List.copyOf(accountMap.values());
    }
}
