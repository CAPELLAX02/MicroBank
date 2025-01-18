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
    public ResponseEntity<BaseApiResponse<AccountResponse>> createAccount(
            @RequestBody @Valid CreateAccountRequest request
    ) {
        return ResponseEntity.ok(accountService.createAccount(request));
    }

    @PatchMapping("/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<AccountResponse>> updateAccountBalance(
            @RequestBody UpdateBalanceRequest request
    ) {
        return ResponseEntity.ok(accountService.updateAccountBalance(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<List<AccountResponse>>> getCurrentUsersAccounts() {
        return ResponseEntity.ok(accountService.getCurrentUsersAccounts());
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<AccountResponse>> getCurrentUsersAccountById(
            @PathVariable UUID accountId
    ) {
        return ResponseEntity.ok(accountService.getCurrentUsersAccountById(accountId));
    }

    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseApiResponse<String>> deleteOwnAccount(
            @PathVariable UUID accountId
    ) {
        return ResponseEntity.ok(accountService.deleteOwnAccount(accountId));
    }

    @GetMapping("/admin/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<AccountResponse>>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/admin/accounts/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<AccountResponse>> getAccountById(
            @PathVariable UUID accountId
    ) {
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }

    @GetMapping("/admin/users/{userId}/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<AccountResponse>>> getAccountsByUserId(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    @PatchMapping("/admin/accounts/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<AccountResponse>> updateAccountStatus(
            @RequestBody UpdateAccountStatusRequest request
    ) {
        return ResponseEntity.ok(accountService.updateAccountStatus(request));
    }

    @DeleteMapping("/admin/accounts/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<String>> deleteAccount(
            @PathVariable UUID accountId
    ) {
        return ResponseEntity.ok(accountService.deleteAccount(accountId));
    }


}
