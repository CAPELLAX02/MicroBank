package com.microbank.transaction.controller;

import com.microbank.transaction.dto.request.CreateTransactionRequest;
import com.microbank.transaction.dto.response.TransactionResponse;
import com.microbank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
            @PathVariable String IBAN
    ) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountIBAN(IBAN));
    }

}
