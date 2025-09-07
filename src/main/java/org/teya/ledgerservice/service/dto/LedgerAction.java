package org.teya.ledgerservice.service.dto;

import org.teya.ledgerservice.model.Currency;
import org.teya.ledgerservice.model.TransactionType;

import java.math.BigDecimal;

public record LedgerAction(
        String accountId,
        TransactionType transactionType,
        Currency currency,
        BigDecimal amount
) {
}
