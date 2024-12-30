package com.microbank.transaction.feign;

import com.microbank.transaction.dto.request.UpdateBalanceRequest;
import com.microbank.transaction.dto.response.AccountResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "account-service", url = "http://localhost:8084/api/v1/account")
public interface AccountServiceClient {

    @GetMapping("/{accountId}")
    AccountResponse getAccountByAccountId(@Valid @PathVariable UUID accountId);

    @GetMapping("/{IBAN}")
    AccountResponse getAccountByIBAN(@Valid @PathVariable String IBAN);

    @PutMapping("/balance")
    void updateBalance(@Valid @RequestBody UpdateBalanceRequest request);

}
