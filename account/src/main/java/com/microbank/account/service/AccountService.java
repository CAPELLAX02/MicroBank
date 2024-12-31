package com.microbank.account.service;

import com.microbank.account.dto.request.CreateAccountRequest;
import com.microbank.account.dto.request.UpdateBalanceRequest;
import com.microbank.account.dto.response.AccountResponse;

import java.util.List;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request, String keycloakUserId);
    void updateBalance(UpdateBalanceRequest request);
    AccountResponse getAccountByIBAN(String IBAN);

    List<AccountResponse> getAllAccountsByKeycloakId(String keycloakId);
    List<String> getIbansByKeycloakId(String keycloakId);


}
