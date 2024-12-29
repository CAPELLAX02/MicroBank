package com.microbank.account.service;

import com.microbank.account.AccountResponse;
import com.microbank.account.CreateAccountRequest;

import java.util.List;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);
    List<AccountResponse> getAllAccountsByUserId(Long userId);

}
