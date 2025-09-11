package org.teya.ledgerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.teya.ledgerservice.model.Currency;
import org.teya.ledgerservice.model.TransactionType;
import org.teya.ledgerservice.service.dto.LedgerAction;
import org.teya.ledgerservice.service.impl.LegerActionServiceImpl;
import org.teya.ledgerservice.service.impl.LegerQueryServiceImpl;
import org.teya.ledgerservice.service.impl.LegerSessionServiceImpl;
import org.teya.ledgerservice.store.AccountStore;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class LedgerSessionTest {

    private AccountStore store = new AccountStore();

    private LedgerSessionService ledgerSessionService = new LegerSessionServiceImpl(store);

    private LedgerQueryService ledgerQueryService = new LegerQueryServiceImpl(store);

    private LegerActionService legerActionService = new LegerActionServiceImpl(store);

    @Test
    void test_session_rollback(){
        //Start trans
        var accountId = "test-account-1";
        ledgerSessionService.startSession(accountId);
        //aA
        legerActionService.performAction(new LedgerAction(
                accountId,
                TransactionType.CREDIT,
                Currency.GBP,
                BigDecimal.valueOf(10)
        ));
        legerActionService.performAction(new LedgerAction(
                accountId,
                TransactionType.DEBIT,
                Currency.GBP,
                BigDecimal.valueOf(7)
        ));
        //roll
        var balance = ledgerQueryService.checkBalance(accountId);
        assertEquals("3", balance.toString());

        ledgerSessionService.rollback(accountId);

        balance = ledgerQueryService.checkBalance(accountId);
        assertEquals("0", balance.toString());
    }

}
