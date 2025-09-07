package org.teya.ledgerservice.api.dto;

import org.teya.ledgerservice.model.Currency;

public record AccountItemResponse(
        String accountId,
        String accountNumber,
        String sortCode,
        Currency currency
) {
}
