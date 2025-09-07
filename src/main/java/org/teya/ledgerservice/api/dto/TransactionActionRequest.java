package org.teya.ledgerservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import org.teya.ledgerservice.validation.ValueOfEnum;
import org.teya.ledgerservice.model.TransactionType;
import org.teya.ledgerservice.model.Currency;

public record TransactionActionRequest(
        @NotBlank @Size(max = 36, message = "Invalid accountId length") String accountId,
        @NotBlank @ValueOfEnum(enumClass = TransactionType.class) String transactionType,
        @NotBlank @ValueOfEnum(enumClass = Currency.class) String currency,
        @NotNull @PositiveOrZero BigDecimal amount
) {
}
