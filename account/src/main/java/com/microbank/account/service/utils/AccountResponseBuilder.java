package com.microbank.account.service.utils;

import com.microbank.account.dto.response.AccountResponse;
import com.microbank.account.model.Account;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountResponseBuilder {

    public AccountResponse buildAccountResponse(Account a) {
        return new AccountResponse(
                a.getId(),
                a.getIBAN(),
                a.getBalance(),
                a.isBlocked(),
                a.getOwnerName(),
                a.getOwnerId(),
                a.getOwnerEmail()
        );
    }

    public List<AccountResponse> buildAccountResponses(List<Account> accounts) {
        List<AccountResponse> accountResponses = new ArrayList<>();
        for (Account a : accounts) {
            accountResponses.add(buildAccountResponse(a));
        }
        return accountResponses;
    }

}
