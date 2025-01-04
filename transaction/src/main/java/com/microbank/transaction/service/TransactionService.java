package com.microbank.transaction.service;

import com.microbank.transaction.dto.request.CreateTransactionRequest;
import com.microbank.transaction.dto.response.TransactionDetailsResponse;
import com.microbank.transaction.dto.response.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionResponse createTransaction(CreateTransactionRequest transaction);
    List<TransactionResponse> getMyTransactionsByAccountIBAN(String sourceAccountIBAN, String keycloakId);
    List<TransactionResponse> getMyAllTransactions(String keycloakId);
    TransactionDetailsResponse getTransactionDetailsById(String transactionId);

    TransactionResponse getTransactionById(UUID transactionId);
    List<TransactionResponse> getAllTransactions();

}
