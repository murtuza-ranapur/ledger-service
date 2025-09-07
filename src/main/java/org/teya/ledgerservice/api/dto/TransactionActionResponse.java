package org.teya.ledgerservice.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionActionResponse(
        String transactionId
) {
}
