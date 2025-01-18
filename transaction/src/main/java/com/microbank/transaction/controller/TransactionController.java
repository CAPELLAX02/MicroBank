package com.microbank.transaction.controller;

import com.microbank.transaction.dto.request.CreateTransactionRequest;
import com.microbank.transaction.dto.response.TransactionResponse;
import com.microbank.transaction.response.BaseApiResponse;
import com.microbank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<TransactionResponse>> createTransaction(
            @RequestBody @Valid CreateTransactionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<List<TransactionResponse>>> getCurrentUsersAllTransactions() {
        return ResponseEntity.ok(transactionService.getCurrentUsersTransactions());
    }

    @GetMapping("/me/{transactionId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<TransactionResponse>> getCurrentUsersTransactionById(
            @PathVariable UUID transactionId
    ) {
        return ResponseEntity.ok(transactionService.getCurrentUsersTransactionById(transactionId));
    }

    @GetMapping("/me/accounts/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<List<TransactionResponse>>> getCurrentUsersTransactionsByAccountId(
            @PathVariable UUID accountId
    ) {
        return ResponseEntity.ok(transactionService.getCurrentUsersTransactionsByAccountId(accountId));
    }


    @GetMapping("/admin/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<TransactionResponse>>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/admin/transactions/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<TransactionResponse>> getTransactionById(
            @PathVariable UUID transactionId
    ) {
        return ResponseEntity.ok(transactionService.getTransactionById(transactionId));
    }

    @GetMapping("/admin/accounts/{accountId}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<TransactionResponse>>> getTransactionsByAccountId(
            @PathVariable UUID accountId
    ) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
    }

    @GetMapping("/admin/users/{userId}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<TransactionResponse>>> getTransactionsByUserId(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
    }

}
