package com.microbank.transaction.feign;

import com.microbank.transaction.config.FeignConfig;
import com.microbank.transaction.dto.request.UpdateBalanceRequest;
import com.microbank.transaction.dto.response.AccountResponse;
import com.microbank.transaction.response.BaseApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "account-service",
        url = "http://localhost:8082/api/v1/accounts",
        configuration = FeignConfig.class
)
public interface AccountServiceClient {

    @GetMapping
    BaseApiResponse<List<AccountResponse>> getCurrentUsersAccounts();

    @GetMapping("/admin/accounts/{accountId}")
    BaseApiResponse<AccountResponse> getAccountById(@PathVariable("accountId") UUID accountId);

    @GetMapping("/iban/{iban}")
    BaseApiResponse<AccountResponse> getAccountByIban(@PathVariable("iban") String iban);

    @GetMapping("/{accountId}")
    BaseApiResponse<AccountResponse> getCurrentUsersAccountById(@PathVariable("accountId") UUID accountId);

    @PutMapping("/balance")
    BaseApiResponse<AccountResponse> updateAccountBalance(@RequestBody UpdateBalanceRequest request);

    @GetMapping("/admin/users/{userId}/accounts")
    BaseApiResponse<List<AccountResponse>> getAccountsByUserId(@PathVariable("userId") UUID userId);

}
