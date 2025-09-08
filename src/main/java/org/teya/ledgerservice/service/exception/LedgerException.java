package org.teya.ledgerservice.service.exception;

/**
 * Domain/service level exception for ledger operations.
 */
public class LedgerException extends RuntimeException {
    public LedgerException(String message) {
        super(message);
    }

    public LedgerException(String message, Throwable cause) {
        super(message, cause);
    }
}

