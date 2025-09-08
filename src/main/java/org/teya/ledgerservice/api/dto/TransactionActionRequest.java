package org.teya.ledgerservice.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import org.teya.ledgerservice.validation.ValueOfEnum;
import org.teya.ledgerservice.model.TransactionType;
import org.teya.ledgerservice.model.Currency;

public record TransactionActionRequest(
        @NotBlank @Size(max = 36, message = "Invalid accountId length") String accountId,
        @NotBlank @ValueOfEnum(enumClass = TransactionType.class) String transactionType,
        @NotBlank @ValueOfEnum(enumClass = Currency.class) String currency,
        @NotNull @Digits(integer = 18, fraction = 2) @PositiveOrZero BigDecimal amount
) {
}
