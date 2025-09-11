package org.teya.ledgerservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.teya.ledgerservice.service.LedgerSessionService;
import org.teya.ledgerservice.service.exception.LedgerException;
import org.teya.ledgerservice.store.AccountStore;

@Service
@RequiredArgsConstructor
public class LegerSessionServiceImpl implements LedgerSessionService {
    private final AccountStore accountStore;

    @Override
    public boolean startSession(String accountId) {
        var accountOp = accountStore.getAccount(accountId);
        if(accountOp.isEmpty()){
            throw new LedgerException("Account or user not found");
        }
        var account = accountOp.get();
        account.setSessionOn(true);
        return true;
    }

    @Override
    public boolean commit(String accountId) {
        var accountOp = accountStore.getAccount(accountId);
        if(accountOp.isEmpty()){
            throw new LedgerException("Account or user not found");
        }
        var account = accountOp.get();
        return account.commitSession();
    }

    @Override
    public boolean rollback(String accountId) {
        var accountOp = accountStore.getAccount(accountId);
        if(accountOp.isEmpty()){
            throw new LedgerException("Account or user not found");
        }
        var account = accountOp.get();
        account.rollbackSession();
        return true;
    }
}
