package com.microbank.account.service.impl;

import com.microbank.account.*;
import com.microbank.account.service.AccountService;
import com.microbank.account.service.utils.IbanGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AuthServiceClient authServiceClient;

    public AccountServiceImpl(AccountRepository accountRepository, AuthServiceClient authServiceClient) {
        this.accountRepository = accountRepository;
        this.authServiceClient = authServiceClient;
    }

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        UserResponse user = authServiceClient.getUserById(request.userId());
        if (user == null) {
            throw new RuntimeException("User not found in the auth service.");
        }

        Account account = new Account();
        account.setIBAN(IbanGenerator.generateIBAN());
        account.setOwnerName(request.ownerName());
        account.setBalance(request.initialBalance());
        account.setUserId(request.userId());

        account = accountRepository.save(account);

        return new AccountResponse(
                account.getId(),
                account.getIBAN(),
                account.getOwnerName(),
                account.getBalance(),
                account.getUserId()
        );
    }

    @Override
    public List<AccountResponse> getAllAccountsByUserId(Long userId) {
        return accountRepository.findAllByUserId(userId)
                .stream()
                .map(account -> new AccountResponse(
                        account.getId(),
                        account.getIBAN(),
                        account.getOwnerName(),
                        account.getBalance(),
                        account.getUserId()
                ))
                .collect(Collectors.toList());
    }

}
