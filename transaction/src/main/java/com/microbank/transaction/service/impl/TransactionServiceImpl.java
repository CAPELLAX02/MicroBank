package com.microbank.transaction.service.impl;

import com.microbank.transaction.dto.event.TransactionEvent;
import com.microbank.transaction.dto.request.CreateTransactionRequest;
import com.microbank.transaction.dto.request.UpdateBalanceRequest;
import com.microbank.transaction.dto.response.TransactionResponse;
import com.microbank.transaction.feign.AccountServiceClient;
import com.microbank.transaction.feign.AuthServiceClient;
import com.microbank.transaction.model.Transaction;
import com.microbank.transaction.repository.TransactionRepository;
import com.microbank.transaction.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private final AuthServiceClient authServiceClient;
    private final RabbitTemplate rabbitTemplate;
    private final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountServiceClient accountServiceClient, RabbitTemplate rabbitTemplate, AuthServiceClient authServiceClient) {
        this.transactionRepository = transactionRepository;
        this.accountServiceClient = accountServiceClient;
        this.rabbitTemplate = rabbitTemplate;
        this.authServiceClient = authServiceClient;
    }

    @Override
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        var sourceAccount = accountServiceClient.getAccountByIBAN(request.sourceAccountIBAN());
        var targetAccount = accountServiceClient.getAccountByIBAN(request.targetAccountIBAN());

        logger.info("Source Account Response: {}", sourceAccount);
        logger.info("Target Account Response: {}", targetAccount);

        accountServiceClient.updateBalance(
                new UpdateBalanceRequest(request.sourceAccountIBAN(), request.amount(), false)
        );

        accountServiceClient.updateBalance(
                new UpdateBalanceRequest(request.targetAccountIBAN(), request.amount(), true)
        );

        Transaction sourceTransaction = new Transaction();
        sourceTransaction.setSourceAccountIBAN(request.sourceAccountIBAN());
        sourceTransaction.setTargetAccountIBAN(request.targetAccountIBAN());
        sourceTransaction.setAmount(request.amount());
        sourceTransaction.setTimestamp(LocalDateTime.now());
        sourceTransaction.setType("TRANSFER");
        transactionRepository.save(sourceTransaction);

        Transaction targetTransaction = new Transaction();
        targetTransaction.setSourceAccountIBAN(request.targetAccountIBAN());
        targetTransaction.setTargetAccountIBAN(request.sourceAccountIBAN());
        targetTransaction.setAmount(request.amount());
        targetTransaction.setTimestamp(LocalDateTime.now());
        targetTransaction.setType("RECEIVED");
        transactionRepository.save(targetTransaction);

        TransactionEvent transactionEvent = new TransactionEvent(
                sourceTransaction.getSourceAccountIBAN(),
                sourceTransaction.getTargetAccountIBAN(),
                sourceAccount.email(),
                targetAccount.email(),
                sourceAccount.ownerName(),
                targetAccount.ownerName(),
                sourceTransaction.getAmount(),
                sourceTransaction.getTimestamp()
        );

        logger.info("Transaction Event: {}", transactionEvent);

        rabbitTemplate.convertAndSend("transaction-queue", transactionEvent);

        return new TransactionResponse(
                sourceTransaction.getId(),
                sourceTransaction.getSourceAccountIBAN(),
                sourceTransaction.getTargetAccountIBAN(),
                sourceTransaction.getAmount(),
                sourceTransaction.getTimestamp(),
                sourceTransaction.getType()
        );
    }

    @Override
    public List<TransactionResponse> getMyTransactionsByAccountIBAN(String IBAN, String keycloakId) {
        var account = accountServiceClient.getAccountByIBAN(IBAN);

        if (account == null) {
            throw new RuntimeException("Account not found");
        }

        var user = authServiceClient.getUserById(account.userId());
        if (!user.keycloakId().equals(keycloakId)) {
            throw new RuntimeException("Access denied: This account does not belong to the authenticated user.");
        }

        List<Transaction> transactions = transactionRepository.findAllBySourceAccountIBAN(IBAN)
                .orElseThrow(() -> new RuntimeException("No transactions found for account IBAN: " + IBAN));

        return transactions.stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getSourceAccountIBAN(),
                        transaction.getTargetAccountIBAN(),
                        transaction.getAmount(),
                        transaction.getTimestamp(),
                        transaction.getType()
                ))
                .collect(Collectors.toList());
    }


    @Override
    public List<TransactionResponse> getMyAllTransactions(String keycloakId) {
        List<String> userIbans = accountServiceClient.getIbansByKeycloakId(keycloakId);

        if (userIbans.isEmpty()) {
            throw new RuntimeException("No accounts found for the user.");
        }

        List<Transaction> transactions = transactionRepository.findAllBySourceAccountIBANIn(userIbans);

        return transactions.stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getSourceAccountIBAN(),
                        transaction.getTargetAccountIBAN(),
                        transaction.getAmount(),
                        transaction.getTimestamp(),
                        transaction.getType()
                ))
                .collect(Collectors.toList());
    }

}
