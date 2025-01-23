package com.microbank.transaction.feign.fallback;

import com.microbank.transaction.dto.request.UpdateBalanceRequest;
import com.microbank.transaction.dto.response.AccountResponse;
import com.microbank.transaction.feign.AccountServiceClient;
import com.microbank.transaction.response.BaseApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AccountServiceClientFallback implements AccountServiceClient {

    @Override
    public BaseApiResponse<List<AccountResponse>> getCurrentUsersAccounts() {
        return new BaseApiResponse<>(HttpStatus.SERVICE_UNAVAILABLE.value(), "Account service is temporarily unavailable.", null);
    }

    @Override
    public BaseApiResponse<AccountResponse> getAccountById(UUID accountId) {
        return new BaseApiResponse<>(HttpStatus.SERVICE_UNAVAILABLE.value(), "Account service is temporarily unavailable.", null);
    }

    @Override
    public BaseApiResponse<AccountResponse> getCurrentUsersAccountById(UUID accountId) {
        return new BaseApiResponse<>(HttpStatus.SERVICE_UNAVAILABLE.value(), "SHDFGIJKLOSDHFIOKLSDJHFILOSK service is temporarily unavailable.", null);
    }

    @Override
    public BaseApiResponse<AccountResponse> updateAccountBalance(UpdateBalanceRequest request) {
        return new BaseApiResponse<>(HttpStatus.SERVICE_UNAVAILABLE.value(), "Account service is temporarily unavailable.", null);
    }

    @Override
    public BaseApiResponse<List<AccountResponse>> getAccountsByUserId(UUID userId) {
        return new BaseApiResponse<>(HttpStatus.SERVICE_UNAVAILABLE.value(), "Account service is temporarily unavailable.", null);
    }
}
