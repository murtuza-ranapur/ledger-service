package org.teya.ledgerservice.service;

public interface LedgerSessionService {
    boolean startSession(String accountId);
    boolean commit(String accountId);
    boolean rollback(String accountId);
}
