package com.microbank.account.controller;

import com.microbank.account.dto.request.CreateAccountRequest;
import com.microbank.account.dto.request.UpdateBalanceRequest;
import com.microbank.account.dto.response.AccountResponse;
import com.microbank.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @RequestBody @Valid CreateAccountRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakUserId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(accountService.createAccount(request, keycloakUserId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByUserId(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(accountService.getAllAccountsByUserId(userId));
    }

    @PutMapping("/balance")
    public ResponseEntity<Void> updateBalance(
            @RequestBody @Valid UpdateBalanceRequest request
    ) {
        accountService.updateBalance(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable Long accountId
    ) {
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }


}
