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
        accountServiceClient.getAccountByIBAN(request.sourceAccountIBAN());
        accountServiceClient.getAccountByIBAN(request.targetAccountIBAN());

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
    public List<TransactionResponse> getTransactionsByAccountIBAN(String IBAN) {
        var account = accountServiceClient.getAccountByIBAN(IBAN);

        if (account == null) {
            throw new RuntimeException("Account not found");
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

}
