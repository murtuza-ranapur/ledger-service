package org.teya.ledgerservice.api.dto;

public record TransactionItemResponse(
        String transactionId,
        String transactionType,
        String currency,
        String amount,
        String transactionTime
) {
}
