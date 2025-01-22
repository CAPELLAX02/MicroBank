package com.microbank.document.service.utils;

import com.microbank.document.dto.response.TransactionDocumentResponse;
import com.microbank.document.model.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionDocumentResponseBuilder {

    public TransactionDocumentResponse buildTransactionDocumentResponse(Document d) {
        return new TransactionDocumentResponse(
                d.getDocumentUrl(),
                d.getDocumentName(),
                d.getTransactionId(),
                d.getSenderAccountIban(),
                d.getReceiverAccountIban(),
                d.getSenderAccountOwnerName(),
                d.getReceiverAccountOwnerName(),
                d.getAmount(),
                d.getDescription(),
                d.getTimestamp()

        );
    }

    public List<TransactionDocumentResponse> buildTransactionDocumentResponses(List<Document> documents) {
        List<TransactionDocumentResponse> transactionDocumentResponses = new ArrayList<>();
        for (Document d : documents) {
            transactionDocumentResponses.add(buildTransactionDocumentResponse(d));
        }
        return transactionDocumentResponses;
    }

}
