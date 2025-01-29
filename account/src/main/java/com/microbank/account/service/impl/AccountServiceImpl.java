package com.microbank.account.service.impl;

import com.microbank.account.dto.request.CreateAccountRequest;
import com.microbank.account.dto.request.UpdateAccountStatusRequest;
import com.microbank.account.dto.request.UpdateBalanceRequest;
import com.microbank.account.dto.response.AccountResponse;
import com.microbank.account.exceptions.CustomException;
import com.microbank.account.exceptions.NotFoundException;
import com.microbank.account.exceptions.UnauthorizedException;
import com.microbank.account.feign.AuthServiceClient;
import com.microbank.account.model.Account;
import com.microbank.account.repository.AccountRepository;
import com.microbank.account.response.BaseApiResponse;
import com.microbank.account.service.AccountService;
import com.microbank.account.service.utils.AccountResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final AuthServiceClient authServiceClient;
    private final AccountResponseBuilder accountResponseBuilder;

    public AccountServiceImpl(
            AccountRepository accountRepository,
            AuthServiceClient authServiceClient,
            AccountResponseBuilder accountResponseBuilder
    ) {
        this.accountRepository = accountRepository;
        this.authServiceClient = authServiceClient;
        this.accountResponseBuilder = accountResponseBuilder;
    }

    private static String generateIBAN() {
        return "MB" + (100000000000L + (long) (Math.random() * 899999999999L));
    }

    @Override
    public BaseApiResponse<AccountResponse> createAccount(CreateAccountRequest request) {
        try {
            var userResponse = authServiceClient.getCurrentUser();

            if (userResponse == null || userResponse.getData() == null) {
                return new BaseApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "User not authenticated.", null);
            }

            var user = userResponse.getData();

            Account account = new Account();
            account.setIBAN(generateIBAN());
            account.setOwnerName((user.firstName() + " " + user.lastName()).toUpperCase());
            account.setBalance(request.initialBalance());
            account.setOwnerId(user.id());
            account.setOwnerEmail(user.email());

            account = accountRepository.save(account);

            AccountResponse response = accountResponseBuilder.buildAccountResponse(account);

            return new BaseApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Account created successfully.",
                    response
            );

        } catch (Exception e) {
            log.error("Error creating account: {}", e.getMessage(), e);
            return new BaseApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create account.", null);
        }
    }

    @Override
    public BaseApiResponse<AccountResponse> updateAccountBalance(UpdateBalanceRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found with ID: " + request.accountId()));

        if (account.isBlocked()) {
            throw new CustomException("This account is blocked.");
        }

        BigDecimal newBalance = request.isDeposit()
                ? account.getBalance().add(request.amount())
                : account.getBalance().subtract(request.amount());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException("Insufficient balance");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);

        AccountResponse response = accountResponseBuilder.buildAccountResponse(account);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Account balance updated successfully.",
                response
        );
    }

    @Override
    public BaseApiResponse<List<AccountResponse>> getCurrentUsersAccounts() {
        var user = authServiceClient.getCurrentUser();
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        List<Account> accounts = accountRepository.findAllByOwnerId(user.getData().id());
        List<AccountResponse> response = accountResponseBuilder.buildAccountResponses(accounts);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Current users' accounts retrieved successfully.",
                response
        );
    }

    @Override
    public BaseApiResponse<String> getIbanByAccountId(UUID accountId) {
        String response = accountRepository.findById(accountId)
                .map(Account::getIBAN)
                .orElseThrow(() -> new CustomException("IBAN of the account (" + accountId + ") not found"));

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "IBAN retrieved successfully for the account with the ID: " + accountId,
                response
        );
    }

    @Override
    public BaseApiResponse<AccountResponse> getAccountByIban(String iban) {
        Account account = accountRepository.findByIBAN(iban)
                .orElseThrow(() -> new NotFoundException("Account not found with IBAN: " + iban));
        AccountResponse response = accountResponseBuilder.buildAccountResponse(account);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Account with the IBAN: " + iban + " retrieved successfully.",
                response
        );
    }

    @Override
    public BaseApiResponse<AccountResponse> getCurrentUsersAccountById(UUID accountId) {
        var user = authServiceClient.getCurrentUser();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with ID: " + accountId));

        if (!account.getOwnerId().equals(user.getData().id())) {
            throw new UnauthorizedException("You are not authorized to view this account.");
        }

        AccountResponse response = accountResponseBuilder.buildAccountResponse(account);

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Account with the ID: " + accountId + " retrieved successfully.",
                response
        );
    }

    @Override
    public BaseApiResponse<String> deleteOwnAccount(UUID accountId) {
        var user = authServiceClient.getCurrentUser();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with ID: " + accountId));

        if (!account.getOwnerId().equals(user.getData().id())) {
            throw new UnauthorizedException("You are not authorized to delete this account.");
        }

        accountRepository.delete(account);

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Account with the ID: " + accountId + " has been deleted",
                null
        );
    }

    @Override
    public BaseApiResponse<List<AccountResponse>> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        if (accounts.isEmpty()) {
            throw new NotFoundException("No accounts found.");
        }
        List<AccountResponse> accountResponseList = accountResponseBuilder.buildAccountResponses(accounts);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "All accounts retrieved successfully.",
                accountResponseList
        );
    }

    @Override
    public BaseApiResponse<List<AccountResponse>> getAccountsByUserId(UUID userId) {
        List<Account> usersAccounts = accountRepository.findAllByOwnerId(userId);

        if (usersAccounts.isEmpty()) {
            throw new NotFoundException("No accounts found associated with the user with ID: " + userId);
        }

        List<AccountResponse> accountResponses = accountResponseBuilder.buildAccountResponses(usersAccounts);

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Account belong to the user with ID: " + userId + " retrieved successfully.",
                accountResponses
        );
    }

    @Override
    public BaseApiResponse<AccountResponse> getAccountById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with ID: " + accountId));

        AccountResponse accountResponse = accountResponseBuilder.buildAccountResponse(account);

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Account with the ID: " + accountId + " retrieved successfully.",
                accountResponse
        );
    }

    @Override
    public BaseApiResponse<AccountResponse> updateAccountStatus(UpdateAccountStatusRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found with ID: " + request.accountId()));

        account.setBlocked(request.isBlocked());
        accountRepository.save(account);

        AccountResponse accountResponse = accountResponseBuilder.buildAccountResponse(account);

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Status of the account with the ID: " + request.accountId() + " has been updated",
                accountResponse
        );
    }

    @Override
    public BaseApiResponse<String> deleteAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with ID: " + accountId));

        accountRepository.delete(account);

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Account with the ID: " + accountId + " has been deleted",
                null
        );
    }
}
