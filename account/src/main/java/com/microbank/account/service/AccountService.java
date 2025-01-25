package com.microbank.account.service;

import com.microbank.account.dto.request.CreateAccountRequest;
import com.microbank.account.dto.request.UpdateAccountStatusRequest;
import com.microbank.account.dto.request.UpdateBalanceRequest;
import com.microbank.account.dto.response.AccountResponse;
import com.microbank.account.response.BaseApiResponse;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    BaseApiResponse<AccountResponse> createAccount(CreateAccountRequest request);
    BaseApiResponse<AccountResponse> updateAccountBalance(UpdateBalanceRequest request);
    BaseApiResponse<List<AccountResponse>> getCurrentUsersAccounts();
    BaseApiResponse<AccountResponse> getCurrentUsersAccountById(UUID accountId);
    BaseApiResponse<String> deleteOwnAccount(UUID accountId);

    BaseApiResponse<String> getIbanByAccountId(UUID accountId);
    BaseApiResponse<AccountResponse> getAccountByIban(String iban);

    BaseApiResponse<List<AccountResponse>> getAllAccounts();
    BaseApiResponse<List<AccountResponse>> getAccountsByUserId(UUID userId);
    BaseApiResponse<AccountResponse> getAccountById(UUID accountId);
    BaseApiResponse<AccountResponse> updateAccountStatus(UpdateAccountStatusRequest request);
    BaseApiResponse<String> deleteAccount(UUID accountId);

}
