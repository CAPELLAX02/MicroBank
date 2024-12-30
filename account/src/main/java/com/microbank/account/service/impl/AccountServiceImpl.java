package com.microbank.account.service.impl;

import com.microbank.account.dto.request.CreateAccountRequest;
import com.microbank.account.dto.request.UpdateBalanceRequest;
import com.microbank.account.dto.response.AccountResponse;
import com.microbank.account.dto.response.UserResponse;
import com.microbank.account.feign.AuthServiceClient;
import com.microbank.account.model.Account;
import com.microbank.account.repository.AccountRepository;
import com.microbank.account.service.AccountService;
import com.microbank.account.service.utils.IbanGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
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
    public AccountResponse createAccount(CreateAccountRequest request, String keycloakId) {
        UserResponse user = authServiceClient.getUserByKeycloakId(keycloakId);
        if (user == null) {
            throw new RuntimeException("User not found in the auth service.");
        }

        Account account = new Account();
        account.setIBAN(IbanGenerator.generateIBAN());
        account.setOwnerName(user.firstName() + " " + user.lastName());
        account.setBalance(request.initialBalance());
        account.setUserId(user.id());
        account.setKeycloakId(keycloakId);

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
        if (!accountRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
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

    @Override
    public void updateBalance(UpdateBalanceRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal newBalance = request.isDeposit()
                ? account.getBalance().add(request.amount())
                : account.getBalance().subtract(request.amount());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    @Override
    public AccountResponse getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return new AccountResponse(
                account.getId(),
                account.getIBAN(),
                account.getOwnerName(),
                account.getBalance(),
                account.getUserId()
        );
    }

}
