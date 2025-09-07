package org.teya.ledgerservice.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AccountBalance {
    private BigDecimal currentBalance;

    public AccountBalance() {
        this.currentBalance = BigDecimal.ZERO;
    }
}
