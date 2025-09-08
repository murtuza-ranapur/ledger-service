package org.teya.ledgerservice.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.teya.ledgerservice.api.dto.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LedgerControllerIntegrationTest {

    private static final String ACCOUNT_ID = "test-account-1";
    private static final String ACCOUNT_ID_2 = "test-account-2";

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<ApiResponse<TransactionActionResponse>> postTransaction(TransactionActionRequest req) {
        return restTemplate.exchange(
                "/v1/accounts/transaction",
                HttpMethod.POST,
                new HttpEntity<>(req),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    void fullTransactionFlow_withInitialFailedDebit_thenCredits_andDebit_validatesBalanceAndTransactions() {
        // 1. Initial debit should fail (insufficient funds)
        var failedDebitReq = new TransactionActionRequest(ACCOUNT_ID, "DEBIT", "GBP", new BigDecimal("1.00"));
        var failedDebitResp = postTransaction(failedDebitReq);
        assertEquals(HttpStatus.BAD_REQUEST, failedDebitResp.getStatusCode());
        assertNotNull(failedDebitResp.getBody());
        assertEquals("FAILED", failedDebitResp.getBody().status());
        assertEquals("Insufficient funds", failedDebitResp.getBody().error());
        assertNull(failedDebitResp.getBody().data());

        // 2. Two credit requests
        var creditReq1 = new TransactionActionRequest(ACCOUNT_ID, "CREDIT", "GBP", new BigDecimal("10.00"));
        var creditResp1 = postTransaction(creditReq1);
        assertEquals(HttpStatus.OK, creditResp1.getStatusCode());
        assertNotNull(creditResp1.getBody());
        assertEquals("SUCCESS", creditResp1.getBody().status());
        assertNotNull(creditResp1.getBody().data());
        assertNotNull(creditResp1.getBody().data().transactionId());

        var creditReq2 = new TransactionActionRequest(ACCOUNT_ID, "CREDIT", "GBP", new BigDecimal("5.00"));
        var creditResp2 = postTransaction(creditReq2);
        assertEquals(HttpStatus.OK, creditResp2.getStatusCode());
        assertNotNull(creditResp2.getBody());
        assertEquals("SUCCESS", creditResp2.getBody().status());
        assertNotNull(creditResp2.getBody().data());
        assertNotNull(creditResp2.getBody().data().transactionId());

        // 3. One debit request (should succeed)
        var debitReq = new TransactionActionRequest(ACCOUNT_ID, "DEBIT", "GBP", new BigDecimal("3.00"));
        var debitResp = postTransaction(debitReq);
        assertEquals(HttpStatus.OK, debitResp.getStatusCode());
        assertNotNull(debitResp.getBody());
        assertEquals("SUCCESS", debitResp.getBody().status());
        assertNotNull(debitResp.getBody().data());
        assertNotNull(debitResp.getBody().data().transactionId());

        // 4. Verify balance (10 + 5 - 3 = 12.00)
        var balanceResp = restTemplate.exchange(
                "/v1/accounts/" + ACCOUNT_ID + "/balance",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<BalanceResponse>>() {}
        );
        assertEquals(HttpStatus.OK, balanceResp.getStatusCode());
        assertNotNull(balanceResp.getBody());
        assertEquals("SUCCESS", balanceResp.getBody().status());
        var balanceData = balanceResp.getBody().data();
        assertNotNull(balanceData);
        assertEquals("12.00", balanceData.balanceAmount());
        assertEquals(ACCOUNT_ID, balanceData.accountId());
        assertEquals("GBP", balanceData.currency());

        // 5. Verify transactions (newest first): DEBIT 3.00, CREDIT 5.00, CREDIT 10.00
        var txResp = restTemplate.exchange(
                "/v1/accounts/" + ACCOUNT_ID + "/transactions?limit=5",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<List<TransactionItemResponse>>>() {}
        );
        assertEquals(HttpStatus.OK, txResp.getStatusCode());
        assertNotNull(txResp.getBody());
        assertEquals("SUCCESS", txResp.getBody().status());
        var txList = txResp.getBody().data();
        assertNotNull(txList);
        assertEquals(3, txList.size());

        // Transaction 1 (latest)
        assertEquals("DEBIT", txList.get(0).transactionType());
        assertEquals("3.00", txList.get(0).amount());

        // Transaction 2
        assertEquals("CREDIT", txList.get(1).transactionType());
        assertEquals("5.00", txList.get(1).amount());

        // Transaction 3
        assertEquals("CREDIT", txList.get(2).transactionType());
        assertEquals("10.00", txList.get(2).amount());
    }

    @Test
    void creditJustBelowLimit_thenSmallCreditReachingLimitFails() {
        // First credit to just below max (100000000000000 - 0.01)
        var nearLimit = new BigDecimal("99999999999999.99");
        var creditNearLimit = new TransactionActionRequest(ACCOUNT_ID_2, "CREDIT", "GBP", nearLimit);
        var resp1 = postTransaction(creditNearLimit);
        assertEquals(HttpStatus.OK, resp1.getStatusCode());
        assertNotNull(resp1.getBody());
        assertEquals("SUCCESS", resp1.getBody().status());

        // Second credit of 0.01 should fail (would reach exact limit)
        var tinyCredit = new TransactionActionRequest(ACCOUNT_ID_2, "CREDIT", "GBP", new BigDecimal("0.01"));
        var resp2 = postTransaction(tinyCredit);
        assertEquals(HttpStatus.BAD_REQUEST, resp2.getStatusCode());
        assertNotNull(resp2.getBody());
        assertEquals("FAILED", resp2.getBody().status());
        assertEquals("Balance limit reached: cannot reach or exceed 100000000000000", resp2.getBody().error());
    }
}
