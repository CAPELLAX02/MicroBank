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

import java.util.List;

@RestController
@RequestMapping("/api/v1/account") // TODO: rename the path: "account" -> "accounts"
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

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakUserId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(accountService.getAllAccountsByKeycloakId(keycloakUserId));
    }

    @PutMapping("/balance")
    public ResponseEntity<Void> updateBalance(
            @RequestBody @Valid UpdateBalanceRequest request
    ) {
        accountService.updateBalance(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{IBAN}")
    public ResponseEntity<AccountResponse> getAccountByIBAN(
            @PathVariable String IBAN
    ) {
        return ResponseEntity.ok(accountService.getAccountByIBAN(IBAN));
    }

    @GetMapping("/keycloak/{keycloakId}/ibans")
    public ResponseEntity<List<String>> getIbansByKeycloakId(@PathVariable String keycloakId) {
        List<String> IBANs = accountService.getIbansByKeycloakId(keycloakId);
        return ResponseEntity.ok(IBANs);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountId) {
        accountService.deleteAccountByAccountId(accountId);
        return ResponseEntity.noContent().build();
    }

}
