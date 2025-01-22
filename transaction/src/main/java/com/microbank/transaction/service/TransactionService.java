package com.microbank.transaction.service;

import com.microbank.transaction.dto.event.TransactionEvent;
import com.microbank.transaction.dto.request.CreateTransactionRequest;
import com.microbank.transaction.dto.response.TransactionResponse;
import com.microbank.transaction.response.BaseApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    BaseApiResponse<TransactionResponse> createTransaction(CreateTransactionRequest request);
    BaseApiResponse<List<TransactionResponse>> getCurrentUsersAllTransactions();
    BaseApiResponse<TransactionResponse> getCurrentUsersTransactionById(UUID transactionId);
    BaseApiResponse<List<TransactionResponse>> getCurrentUsersTransactionsByAccountId(UUID accountId);

    BaseApiResponse<List<TransactionResponse>> getAllTransactions();
    BaseApiResponse<TransactionResponse> getTransactionById(UUID transactionId);
    BaseApiResponse<List<TransactionResponse>> getTransactionsByAccountId(UUID accountId);
    BaseApiResponse<List<TransactionResponse>> getTransactionsByUserId(UUID userId);

}
