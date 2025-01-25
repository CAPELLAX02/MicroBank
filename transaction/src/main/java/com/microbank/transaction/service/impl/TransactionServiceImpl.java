package com.microbank.transaction.service.impl;

import com.microbank.transaction.dto.event.TransactionEvent;
import com.microbank.transaction.dto.request.CreateTransactionRequest;
import com.microbank.transaction.dto.request.UpdateBalanceRequest;
import com.microbank.transaction.dto.response.AccountResponse;
import com.microbank.transaction.dto.response.TransactionResponse;
import com.microbank.transaction.exceptions.CustomException;
import com.microbank.transaction.exceptions.NotFoundException;
import com.microbank.transaction.exceptions.UnauthorizedException;
import com.microbank.transaction.feign.AccountServiceClient;
import com.microbank.transaction.feign.AuthServiceClient;
import com.microbank.transaction.model.Transaction;
import com.microbank.transaction.repository.TransactionRepository;
import com.microbank.transaction.response.BaseApiResponse;
import com.microbank.transaction.service.TransactionService;
import com.microbank.transaction.service.utils.TransactionResponseBuilder;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionResponseBuilder transactionResponseBuilder;
    private final RabbitTemplate rabbitTemplate;
    private final AccountServiceClient accountServiceClient;
    private final AuthServiceClient authServiceClient;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            TransactionResponseBuilder transactionResponseBuilder,
            RabbitTemplate rabbitTemplate,
            AccountServiceClient accountServiceClient,
            AuthServiceClient authServiceClient
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionResponseBuilder = transactionResponseBuilder;
        this.rabbitTemplate = rabbitTemplate;
        this.accountServiceClient = accountServiceClient;
        this.authServiceClient = authServiceClient;
    }

    @Override
    @Transactional
    public BaseApiResponse<TransactionResponse> createTransaction(CreateTransactionRequest request) {
        request.validate();

        var currentUser = authServiceClient.getCurrentUser();
        if (currentUser == null || currentUser.getData() == null) {
            throw new UnauthorizedException("User not authenticated.");
        }

        var senderAccountResponse = accountServiceClient.getCurrentUsersAccountById(request.senderAccountId());
        if (senderAccountResponse == null || senderAccountResponse.getData() == null) {
            throw new UnauthorizedException("Source account does not belong to the current user.");
        }

        AccountResponse senderAccount = accountServiceClient.getAccountById(request.senderAccountId()).getData();
        AccountResponse receiverAccount;

        if (request.receiverAccountId() != null) {
            receiverAccount = accountServiceClient.getAccountById(request.receiverAccountId()).getData();
        } else {
            receiverAccount = accountServiceClient.getAccountByIban(request.receiverAccountIban()).getData();
        }

        if (senderAccount.balance().compareTo(request.amount()) < 0) {
            throw new CustomException("Insufficient balance.");
        }

        accountServiceClient.updateAccountBalance(
                new UpdateBalanceRequest(
                        senderAccount.id(),
                        request.amount(),
                        false
                )
        );

        accountServiceClient.updateAccountBalance(
                new UpdateBalanceRequest(
                        receiverAccount.id(),
                        request.amount(),
                        true
                )
        );

        Transaction transaction = new Transaction();
        transaction.setSenderAccountId(senderAccount.id());
        transaction.setReceiverAccountId(receiverAccount.id());
        transaction.setAmount(request.amount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setDescription(request.description());
        transactionRepository.save(transaction);

        TransactionEvent event = new TransactionEvent(
                transaction.getId(),
                transaction.getSenderAccountId(),
                transaction.getReceiverAccountId(),
                senderAccount.ownerEmail(),
                receiverAccount.ownerEmail(),
                senderAccount.IBAN(),
                receiverAccount.IBAN(),
                senderAccount.ownerName(),
                receiverAccount.ownerName(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTimestamp()
        );

        rabbitTemplate.convertAndSend("transaction-queue", event);

        TransactionResponse transactionResponse = transactionResponseBuilder.buildTransactionResponse(transaction);
        return new BaseApiResponse<>(
                HttpStatus.CREATED.value(),
                "Transaction created successfully.",
                transactionResponse
        );
    }

    @Override
    public BaseApiResponse<List<TransactionResponse>> getCurrentUsersAllTransactions() {
        var currentUser = authServiceClient.getCurrentUser();
        if (currentUser == null || currentUser.getData() == null) {
            throw new UnauthorizedException("User not authenticated.");
        }

        var accountsResponse = accountServiceClient.getCurrentUsersAccounts();
        if (accountsResponse == null || accountsResponse.getData() == null || accountsResponse.getData().isEmpty()) {
            throw new NotFoundException("No accounts found for the current user.");
        }

        List<UUID> accountIds = accountsResponse.getData()
                .stream()
                .map(AccountResponse::id)
                .toList();

        List<Transaction> transactions = transactionRepository.findAllBySenderAccountIdInOrReceiverAccountIdIn(accountIds, accountIds);

        List<TransactionResponse> transactionResponses = transactionResponseBuilder.buildTransactionResponses(transactions);

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Transactions associated with the current user retrieved successfully.",
                transactionResponses
        );
    }

    @Override
    public BaseApiResponse<TransactionResponse> getCurrentUsersTransactionById(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction with the ID: " + transactionId + " not found."));

        var accountsResponse = accountServiceClient.getCurrentUsersAccounts();
        if (accountsResponse == null || accountsResponse.getData() == null || accountsResponse.getData().isEmpty()) {
            throw new NotFoundException("No accounts found for the current user.");
        }

        List<UUID> accountIds = accountsResponse.getData().stream()
                .map(AccountResponse::id)
                .toList();

        if (!accountIds.contains(transaction.getSenderAccountId()) &&
                !accountIds.contains(transaction.getReceiverAccountId())) {
            throw new UnauthorizedException("You are not authorized to access this transaction information.");
        }

        TransactionResponse transactionResponse = transactionResponseBuilder.buildTransactionResponse(transaction);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Transaction with the ID: " + transactionId + " retrieved successfully.",
                transactionResponse
        );
    }

    @Override
    public BaseApiResponse<List<TransactionResponse>> getCurrentUsersTransactionsByAccountId(UUID accountId) {
        var accountResponse = accountServiceClient.getCurrentUsersAccountById(accountId);
        if (accountResponse == null || accountResponse.getData() == null) {
            throw new UnauthorizedException("You are not authorized to access this account's transactions.");
        }

        List<Transaction> transactions = transactionRepository.findAllBySenderAccountIdOrReceiverAccountId(accountId, accountId);

        List<TransactionResponse> transactionResponses = transactionResponseBuilder.buildTransactionResponses(transactions);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Transactions associated with the current user's account with the ID: " + accountId + " retrieved successfully.",
                transactionResponses
        );
    }

    @Override
    public BaseApiResponse<List<TransactionResponse>> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) {
            throw new NotFoundException("No transactions found.");
        }

        List<TransactionResponse> transactionResponses = transactionResponseBuilder.buildTransactionResponses(transactions);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "All transactions retrieved successfully.",
                transactionResponses
        );
    }

    @Override
    public BaseApiResponse<TransactionResponse> getTransactionById(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction with the ID: " + transactionId + " not found."));

        TransactionResponse transactionResponse = transactionResponseBuilder.buildTransactionResponse(transaction);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Transaction with the ID: " + transactionId + " retrieved successfully.",
                transactionResponse
        );
    }

    @Override
    public BaseApiResponse<List<TransactionResponse>> getTransactionsByAccountId(UUID accountId) {
        List<Transaction> transactions = transactionRepository.findAllBySenderAccountIdOrReceiverAccountId(accountId, accountId);

        if (transactions.isEmpty()) {
            throw new NotFoundException("No transactions found for the account with the ID: " + accountId);
        }

        List<TransactionResponse> transactionResponses = transactionResponseBuilder.buildTransactionResponses(transactions);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Transactions associated with the account with the ID: " + accountId + " retrieved successfully.",
                transactionResponses
        );
    }

    @Override
    public BaseApiResponse<List<TransactionResponse>> getTransactionsByUserId(UUID userId) {
        var accountsResponse = accountServiceClient.getAccountsByUserId(userId);
        if (accountsResponse == null || accountsResponse.getData() == null || accountsResponse.getData().isEmpty()) {
            throw new NotFoundException("No accounts found for user with ID: " + userId);
        }

        List<UUID> accountIds = accountsResponse.getData()
                .stream()
                .map(AccountResponse::id)
                .toList();

        List<Transaction> transactions = transactionRepository.findAllBySenderAccountIdInOrReceiverAccountIdIn(accountIds, accountIds);

        if (transactions.isEmpty()) {
            throw new NotFoundException("No transactions found for the user with ID: " + userId);
        }

        List<TransactionResponse> transactionResponses = transactionResponseBuilder.buildTransactionResponses(transactions);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Transactions associated with the user with the ID: " + userId + " retrieved successfully.",
                transactionResponses
        );
    }

}
