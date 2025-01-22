package com.microbank.document.service;

import com.microbank.document.dto.event.TransactionEvent;
import com.microbank.document.dto.response.TransactionDocumentResponse;

import java.util.UUID;

public interface DocumentService {

    void createTransactionDocumentFromEvent(TransactionEvent event);
    TransactionDocumentResponse getTransactionDocumentById(UUID documentId);

    TransactionDocumentResponse getTransactionDocumentByTransactionId(UUID transactionId);

}
