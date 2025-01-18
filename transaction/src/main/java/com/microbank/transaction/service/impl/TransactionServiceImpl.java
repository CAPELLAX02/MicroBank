package com.microbank.transaction.service.impl;

import com.microbank.transaction.dto.event.TransactionRequestedEvent;
import com.microbank.transaction.dto.request.CreateTransactionRequest;
import com.microbank.transaction.dto.request.UpdateBalanceRequest;
import com.microbank.transaction.dto.response.AccountResponse;
import com.microbank.transaction.dto.response.TransactionResponse;
import com.microbank.transaction.exceptions.NotFoundException;
import com.microbank.transaction.exceptions.UnauthorizedException;
import com.microbank.transaction.feign.AccountServiceClient;
import com.microbank.transaction.feign.AuthServiceClient;
import com.microbank.transaction.model.Transaction;
import com.microbank.transaction.model.TransactionStatus;
import com.microbank.transaction.repository.TransactionRepository;
import com.microbank.transaction.response.BaseApiResponse;
import com.microbank.transaction.service.TransactionService;
import com.microbank.transaction.service.utils.TransactionResponseBuilder;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            AccountServiceClient accountServiceClient,
            TransactionResponseBuilder transactionResponseBuilder,
            AuthServiceClient authServiceClient,
            RabbitTemplate rabbitTemplate,
            ApplicationEventPublisher eventPublisher
    ) {
        this.transactionRepository = transactionRepository;
        this.accountServiceClient = accountServiceClient;
        this.transactionResponseBuilder = transactionResponseBuilder;
        this.authServiceClient = authServiceClient;
        this.rabbitTemplate = rabbitTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public BaseApiResponse<TransactionResponse> createTransaction(CreateTransactionRequest request) {
        accountServiceClient.updateAccountBalance(new UpdateBalanceRequest(
                request.sourceAccountId(),
                request.amount(),
                false
        ));

        Transaction transaction = new Transaction();
        transaction.setSourceAccountId(request.sourceAccountId());
        transaction.setReceiverAccountId(request.receiverAccountId());
        transaction.setAmount(request.amount());
        transaction.setStatus(TransactionStatus.NEW);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setDescription(request.description());
        transactionRepository.save(transaction);

        TransactionRequestedEvent event = new TransactionRequestedEvent(
                transaction.getId(),
                transaction.getSourceAccountId(),
                transaction.getReceiverAccountId(),
                transaction.getAmount(),
                "PENDING",
                TransactionStatus.NEW
        );
        rabbitTemplate.convertAndSend("transaction-exchange", "transaction.requested", event);

        TransactionResponse transactionResponse = transactionResponseBuilder.buildTransactionResponse(transaction);

        return new BaseApiResponse<>(
                HttpStatus.CREATED.value(),
                "Transaction created successfully.",
                transactionResponse
        );
    }



    @Override
    public BaseApiResponse<List<TransactionResponse>> getCurrentUsersTransactions() {
        var currentUser = authServiceClient.getCurrentUser();
        if (currentUser == null || currentUser.getData() == null) {
            throw new UnauthorizedException("User not authenticated.");
        }

        var accountsResponse = accountServiceClient.getCurrentUsersAccounts();
        if (accountsResponse == null || accountsResponse.getData() == null || accountsResponse.getData().isEmpty()) {
            throw new NotFoundException("No accounts found for the current user.");
        }

        List<UUID> accountIds = accountsResponse.getData().stream()
                .map(AccountResponse::id)
                .toList();

        List<Transaction> transactions = transactionRepository.findAllBySourceAccountIdIn(accountIds);

        List<TransactionResponse> transactionResponses = transactionResponseBuilder.buildTransactionResponses(transactions);
        return new BaseApiResponse<>(HttpStatus.OK.value(), "Current user's transactions retrieved successfully.", transactionResponses);
    }

    @Override
    public BaseApiResponse<TransactionResponse> getCurrentUsersTransactionById(UUID transactionId) {
        var currentUser = authServiceClient.getCurrentUser();
        if (currentUser == null || currentUser.getData() == null) {
            throw new UnauthorizedException("User not authenticated.");
        }

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found."));

        var accountResponse = accountServiceClient.getCurrentUsersAccountById(transaction.getSourceAccountId());
        if (accountResponse == null || accountResponse.getData() == null) {
            throw new UnauthorizedException("You are not authorized to access this transaction.");
        }

        TransactionResponse transactionResponse = transactionResponseBuilder.buildTransactionResponse(transaction);
        return new BaseApiResponse<>(HttpStatus.OK.value(), "Transaction retrieved successfully.", transactionResponse);
    }

    @Override
    public BaseApiResponse<List<TransactionResponse>> getCurrentUsersTransactionsByAccountId(UUID accountId) {
        var currentUser = authServiceClient.getCurrentUser();
        if (currentUser == null || currentUser.getData() == null) {
            throw new UnauthorizedException("User not authenticated.");
        }

        var accountResponse = accountServiceClient.getCurrentUsersAccountById(accountId);
        if (accountResponse == null || accountResponse.getData() == null) {
            throw new UnauthorizedException("You are not authorized to access this account's transactions.");
        }

        List<Transaction> transactions = transactionRepository.findAllBySourceAccountId(accountId);
        List<TransactionResponse> transactionResponses = transactionResponseBuilder.buildTransactionResponses(transactions);

        return new BaseApiResponse<>(HttpStatus.OK.value(), "Transactions for the account retrieved successfully.", transactionResponses);
    }

    @Override
    public BaseApiResponse<List<TransactionResponse>> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        if (transactions.isEmpty()) {
            throw new NotFoundException("No transactions found.");
        }

        List<TransactionResponse> transactionResponses = transactionResponseBuilder.buildTransactionResponses(transactions);
        return new BaseApiResponse<>(HttpStatus.OK.value(), "All transactions retrieved successfully.", transactionResponses);
    }

    @Override
    public BaseApiResponse<TransactionResponse> getTransactionById(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found."));

        TransactionResponse transactionResponse = transactionResponseBuilder.buildTransactionResponse(transaction);
        return new BaseApiResponse<>(HttpStatus.OK.value(), "Transaction retrieved successfully.", transactionResponse);
    }

    @Override
    public BaseApiResponse<List<TransactionResponse>> getTransactionsByAccountId(UUID accountId) {
        List<Transaction> transactions = transactionRepository.findAllBySourceAccountId(accountId);

        List<TransactionResponse> transactionResponses = transactionResponseBuilder.buildTransactionResponses(transactions);
        return new BaseApiResponse<>(HttpStatus.OK.value(), "Transactions for the account retrieved successfully.", transactionResponses);
    }

    @Override
    public BaseApiResponse<List<TransactionResponse>> getTransactionsByUserId(UUID userId) {
        var accountsResponse = accountServiceClient.getAccountsByUserId(userId);
        if (accountsResponse == null || accountsResponse.getData() == null || accountsResponse.getData().isEmpty()) {
            throw new NotFoundException("No accounts found for user.");
        }

        List<UUID> accountIds = accountsResponse.getData().stream()
                .map(AccountResponse::id)
                .toList();

        List<Transaction> transactions = transactionRepository.findAllBySourceAccountIdIn(accountIds);

        List<TransactionResponse> transactionResponses = transactionResponseBuilder.buildTransactionResponses(transactions);
        return new BaseApiResponse<>(HttpStatus.OK.value(), "Transactions for user retrieved successfully.", transactionResponses);
    }

}
