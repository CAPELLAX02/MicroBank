package com.microbank.transaction.controller;

import com.microbank.transaction.dto.request.CreateTransactionRequest;
import com.microbank.transaction.dto.response.TransactionResponse;
import com.microbank.transaction.response.BaseApiResponse;
import com.microbank.transaction.service.TransactionService;
import jakarta.validation.Valid;
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
    public ResponseEntity<BaseApiResponse<TransactionResponse>> createTransaction(@RequestBody @Valid CreateTransactionRequest request) {
        BaseApiResponse<TransactionResponse> response = transactionService.createTransaction(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<List<TransactionResponse>>> getCurrentUsersAllTransactions() {
        BaseApiResponse<List<TransactionResponse>> response = transactionService.getCurrentUsersAllTransactions();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/me/{transactionId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<TransactionResponse>> getCurrentUsersTransactionById(@PathVariable UUID transactionId) {
        BaseApiResponse<TransactionResponse> response = transactionService.getCurrentUsersTransactionById(transactionId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/me/accounts/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<List<TransactionResponse>>> getCurrentUsersTransactionsByAccountId(@PathVariable UUID accountId) {
        BaseApiResponse<List<TransactionResponse>> response = transactionService.getCurrentUsersTransactionsByAccountId(accountId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/admin/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<TransactionResponse>>> getAllTransactions() {
        BaseApiResponse<List<TransactionResponse>> response = transactionService.getAllTransactions();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/admin/transactions/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<TransactionResponse>> getTransactionById(@PathVariable UUID transactionId) {
        BaseApiResponse<TransactionResponse> response = transactionService.getTransactionById(transactionId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/admin/accounts/{accountId}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<TransactionResponse>>> getTransactionsByAccountId(@PathVariable UUID accountId) {
        BaseApiResponse<List<TransactionResponse>> response = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/admin/users/{userId}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<TransactionResponse>>> getTransactionsByUserId(@PathVariable UUID userId) {
        BaseApiResponse<List<TransactionResponse>> response = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
