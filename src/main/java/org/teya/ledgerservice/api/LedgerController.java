package org.teya.ledgerservice.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.teya.ledgerservice.api.dto.*;
import org.teya.ledgerservice.model.Currency;
import org.teya.ledgerservice.model.TransactionType;
import org.teya.ledgerservice.service.LedgerQueryService;
import org.teya.ledgerservice.service.LegerActionService;
import org.teya.ledgerservice.service.dto.LedgerAction;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
@Slf4j
@Validated
public class LedgerController {
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("0.01");

    private final LedgerQueryService ledgerQueryService;
    private final LegerActionService legerActionService;

    @PostMapping("/transaction")
    public ResponseEntity<ApiResponse<TransactionActionResponse>> performTransaction(@Valid @RequestBody TransactionActionRequest request) {
        if (request.amount().compareTo(MIN_AMOUNT) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure("Invalid amount"));
        }

        var action = new LedgerAction(
                request.accountId(),
                TransactionType.valueOf(request.transactionType().toUpperCase()),
                Currency.valueOf(request.currency().toUpperCase()),
                request.amount()
        );
        try {
            var tx = legerActionService.performAction(action);
            return ResponseEntity.ok(ApiResponse.success(new TransactionActionResponse(tx.getId())));
        } catch (RuntimeException e) {
            log.error("Failed performing transaction", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountItemResponse>>> getAccounts() {
        try {
            var accounts = ledgerQueryService.getAccounts();
            var payload = accounts.stream()
                    .map(a -> new AccountItemResponse(
                            a.getId(),
                            a.getNumber(),
                            a.getSortCode(),
                            a.getCurrency()
                    ))
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(payload));
        } catch (RuntimeException e) {
            log.error("Failed fetching accounts", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage()));
        }
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(
            @PathVariable("accountId") @Size(max = 36, message = "Invalid accountId length") String accountId) {
        try {
            var balance = ledgerQueryService.checkBalance(accountId);
            var payload = new BalanceResponse(
                    accountId,
                    balance.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    Currency.GBP.name()
            );
            return ResponseEntity.ok(ApiResponse.success(payload));
        } catch (RuntimeException e) {
            log.error("Failed fetching balance", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage()));
        }
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<ApiResponse<List<TransactionItemResponse>>> getLatestTransactions(
            @PathVariable("accountId") @Size(max = 36, message = "Invalid accountId length") String accountId,
            @RequestParam(name = "limit", required = false, defaultValue = "5")
            @Min(value = 1, message = "limit must be >= 1") @Max(value = 20, message = "limit must be <= 20") int limit) {
        try {
            var transactions = ledgerQueryService.getTransactions(accountId, limit);
            var payload = transactions.stream()
                    .map(t -> new TransactionItemResponse(
                            t.getId(),
                            t.getTransactionType().name(),
                            t.getCurrency().name(),
                            t.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                            Instant.ofEpochMilli(t.getTransactionTime()).toString()
                    )).toList();
            return ResponseEntity.ok(ApiResponse.success(payload));
        } catch (RuntimeException e) {
            log.error("Failed fetching transactions", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage()));
        }
    }
}
