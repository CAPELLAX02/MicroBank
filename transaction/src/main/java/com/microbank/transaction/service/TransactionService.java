package com.microbank.transaction.service;

import com.microbank.transaction.dto.request.CreateTransactionRequest;
import com.microbank.transaction.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(CreateTransactionRequest transaction);
    List<TransactionResponse> getTransactionsByAccountId(Long accountId);

}
