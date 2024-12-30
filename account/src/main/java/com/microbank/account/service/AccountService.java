package com.microbank.account.service;

import com.microbank.account.dto.request.CreateAccountRequest;
import com.microbank.account.dto.request.UpdateBalanceRequest;
import com.microbank.account.dto.response.AccountResponse;

import java.security.Principal;
import java.util.List;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request, String keycloakUserId);
    List<AccountResponse> getAllAccountsByUserId(Long userId);
    void updateBalance(UpdateBalanceRequest request);
    AccountResponse getAccountById(Long accountId);

}