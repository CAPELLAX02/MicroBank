package com.microbank.transaction.service.utils;

import com.microbank.transaction.dto.response.TransactionResponse;
import com.microbank.transaction.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionResponseBuilder {

    public TransactionResponse buildTransactionResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getReceiverAccountId(),
                t.getReceiverAccountId(),
                t.getAmount(),
                t.getDescription()
        );
    }

    public List<TransactionResponse> buildTransactionResponses(List<Transaction> transactions) {
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (Transaction t : transactions) {
            transactionResponses.add(buildTransactionResponse(t));
        }
        return transactionResponses;
    }

}
