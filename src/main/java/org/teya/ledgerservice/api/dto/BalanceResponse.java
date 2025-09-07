package org.teya.ledgerservice.api.dto;

public record BalanceResponse(
        String accountId,
        String balanceAmount,
        String currency
) {
}
