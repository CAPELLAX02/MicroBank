package com.microbank.account.controller;

import com.microbank.account.dto.request.CreateAccountRequest;
import com.microbank.account.dto.request.UpdateAccountStatusRequest;
import com.microbank.account.dto.request.UpdateBalanceRequest;
import com.microbank.account.dto.response.AccountResponse;
import com.microbank.account.response.BaseApiResponse;
import com.microbank.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<AccountResponse>> createAccount(@RequestBody @Valid CreateAccountRequest request) {
        BaseApiResponse<AccountResponse> response = accountService.createAccount(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/balance")
    public ResponseEntity<BaseApiResponse<AccountResponse>> updateAccountBalance(@RequestBody UpdateBalanceRequest request) {
        BaseApiResponse<AccountResponse> response = accountService.updateAccountBalance(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<List<AccountResponse>>> getCurrentUsersAccounts() {
        BaseApiResponse<List<AccountResponse>> response = accountService.getCurrentUsersAccounts();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<AccountResponse>> getCurrentUsersAccountById(@PathVariable UUID accountId) {
        BaseApiResponse<AccountResponse> response = accountService.getCurrentUsersAccountById(accountId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{accountId}/iban")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<String>> getIbanByAccountId(@PathVariable UUID accountId) {
        BaseApiResponse<String> response = accountService.getIbanByAccountId(accountId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<String>> deleteOwnAccount(@PathVariable UUID accountId) {
        BaseApiResponse<String> response = accountService.deleteOwnAccount(accountId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/admin/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<AccountResponse>>> getAllAccounts() {
        BaseApiResponse<List<AccountResponse>> response = accountService.getAllAccounts();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/admin/accounts/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<AccountResponse>> getAccountById(@PathVariable UUID accountId) {
        BaseApiResponse<AccountResponse> response = accountService.getAccountById(accountId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/admin/users/{userId}/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<AccountResponse>>> getAccountsByUserId(@PathVariable UUID userId) {
        BaseApiResponse<List<AccountResponse>> response = accountService.getAccountsByUserId(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/admin/accounts/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<AccountResponse>> updateAccountStatus(@RequestBody UpdateAccountStatusRequest request) {
        BaseApiResponse<AccountResponse> response = accountService.updateAccountStatus(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/admin/accounts/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<String>> deleteAccount(@PathVariable UUID accountId) {
        BaseApiResponse<String> response = accountService.deleteAccount(accountId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/iban/{iban}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<AccountResponse>> getAccountByIban(@PathVariable String iban) {
        BaseApiResponse<AccountResponse> response = accountService.getAccountByIban(iban);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
