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
import feign.FeignException;
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
    private final AccountServiceClient accountServiceClient;
    private final AuthServiceClient authServiceClient;
    private final RabbitTemplate rabbitTemplate;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            AccountServiceClient accountServiceClient,
            TransactionResponseBuilder transactionResponseBuilder,
            AuthServiceClient authServiceClient,
            RabbitTemplate rabbitTemplate
    ) {
        this.transactionRepository = transactionRepository;
        this.accountServiceClient = accountServiceClient;
        this.transactionResponseBuilder = transactionResponseBuilder;
        this.authServiceClient = authServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public BaseApiResponse<TransactionResponse> createTransaction(CreateTransactionRequest request) {
        var currentUser = authServiceClient.getCurrentUser();
        if (currentUser == null || currentUser.getData() == null) {
            throw new UnauthorizedException("User not authenticated.");
        }

        var accountsResponse = accountServiceClient.getCurrentUsersAccounts();
        if (accountsResponse == null || accountsResponse.getData() == null || accountsResponse.getData().isEmpty()) {
            throw new UnauthorizedException("No accounts found for the current user.");
        }

        List<UUID> userAccountIds = accountsResponse.getData()
                .stream()
                .map(AccountResponse::id)
                .toList();

        if (!userAccountIds.contains(request.sourceAccountId())) {
            throw new UnauthorizedException("You are not authorized to perform transactions from this account.");
        }

        try {
            accountServiceClient.updateAccountBalance(
                    new UpdateBalanceRequest(
                            request.sourceAccountId(),
                            request.amount(),
                            false
                    )
            );

            accountServiceClient.updateAccountBalance(
                    new UpdateBalanceRequest(
                            request.receiverAccountId(),
                            request.amount(),
                            true
                    )
            );

        } catch (FeignException e) {
            if (e.status() == 400 && e.contentUTF8().contains("Insufficient balance")) {
                throw new CustomException("Insufficient balance to complete the transaction.");
            }
            throw e;
        }

        Transaction transaction = new Transaction();
        transaction.setSourceAccountId(request.sourceAccountId());
        transaction.setReceiverAccountId(request.receiverAccountId());
        transaction.setAmount(request.amount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setDescription(request.description());
        transactionRepository.save(transaction);

        String senderIban = accountServiceClient.getIbanByAccountId(request.sourceAccountId()).getData();
        String receiverIban = accountServiceClient.getIbanByAccountId(request.receiverAccountId()).getData();

        TransactionEvent event = new TransactionEvent(
                transaction.getId(),
                transaction.getSourceAccountId(),
                transaction.getReceiverAccountId(),
                senderIban,
                receiverIban,
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

        List<Transaction> transactions = transactionRepository.findAllBySourceAccountIdInOrReceiverAccountIdIn(accountIds, accountIds);

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

        if (!accountIds.contains(transaction.getSourceAccountId()) &&
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

        List<Transaction> transactions = transactionRepository.findAllBySourceAccountIdOrReceiverAccountId(accountId, accountId);

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
        List<Transaction> transactions = transactionRepository.findAllBySourceAccountIdOrReceiverAccountId(accountId, accountId);

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

        List<UUID> accountIds = accountsResponse.getData().stream()
                .map(AccountResponse::id)
                .toList();

        List<Transaction> transactions = transactionRepository.findAllBySourceAccountIdInOrReceiverAccountIdIn(accountIds, accountIds);

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
