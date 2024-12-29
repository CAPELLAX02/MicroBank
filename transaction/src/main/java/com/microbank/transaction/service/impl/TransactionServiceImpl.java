package com.microbank.transaction.service.impl;

import com.microbank.transaction.dto.request.CreateTransactionRequest;
import com.microbank.transaction.dto.request.UpdateBalanceRequest;
import com.microbank.transaction.dto.response.TransactionResponse;
import com.microbank.transaction.feign.AccountServiceClient;
import com.microbank.transaction.model.Transaction;
import com.microbank.transaction.repository.TransactionRepository;
import com.microbank.transaction.service.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountServiceClient accountServiceClient) {
        this.transactionRepository = transactionRepository;
        this.accountServiceClient = accountServiceClient;
    }

    @Override
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        accountServiceClient.getAccountByAccountId(request.sourceAccountId());
        accountServiceClient.getAccountByAccountId(request.targetAccountId());

        accountServiceClient.updateBalance(
                new UpdateBalanceRequest(request.sourceAccountId(), request.amount(), false)
        );

        accountServiceClient.updateBalance(
                new UpdateBalanceRequest(request.targetAccountId(), request.amount(), true)
        );

        Transaction sourceTransaction = new Transaction();
        sourceTransaction.setSourceAccountId(request.sourceAccountId());
        sourceTransaction.setTargetAccountId(request.targetAccountId());
        sourceTransaction.setAmount(request.amount());
        sourceTransaction.setTimestamp(LocalDateTime.now());
        sourceTransaction.setType("TRANSFER");
        transactionRepository.save(sourceTransaction);

        Transaction targetTransaction = new Transaction();
        targetTransaction.setSourceAccountId(request.targetAccountId());
        targetTransaction.setTargetAccountId(request.sourceAccountId());
        targetTransaction.setAmount(request.amount());
        targetTransaction.setTimestamp(LocalDateTime.now());
        targetTransaction.setType("RECEIVED");
        transactionRepository.save(targetTransaction);

        return new TransactionResponse(
                sourceTransaction.getId(),
                sourceTransaction.getSourceAccountId(),
                sourceTransaction.getTargetAccountId(),
                sourceTransaction.getAmount(),
                sourceTransaction.getTimestamp(),
                sourceTransaction.getType()
        );
    }

    @Override
    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
        var account = accountServiceClient.getAccountByAccountId(accountId);

        if (account == null) {
            throw new RuntimeException("Account not found");
        }

        List<Transaction> transactions = transactionRepository.findAllBySourceAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("No transactions found for account id: " + accountId));

        return transactions.stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getSourceAccountId(),
                        transaction.getTargetAccountId(),
                        transaction.getAmount(),
                        transaction.getTimestamp(),
                        transaction.getType()
                ))
                .collect(Collectors.toList());
    }

}
