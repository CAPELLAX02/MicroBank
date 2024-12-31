package com.microbank.transaction.controller;

import com.microbank.transaction.dto.request.CreateTransactionRequest;
import com.microbank.transaction.dto.response.TransactionResponse;
import com.microbank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @GetMapping("/account/{IBAN}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountIBAN(
            @PathVariable String IBAN,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakUserId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(transactionService.getMyTransactionsByAccountIBAN(IBAN, keycloakUserId));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getMyAllTransactions(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakId = jwt.getClaimAsString("sub");
        List<TransactionResponse> transactions = transactionService.getMyAllTransactions(keycloakId);
        return ResponseEntity.ok(transactions);
    }

}
