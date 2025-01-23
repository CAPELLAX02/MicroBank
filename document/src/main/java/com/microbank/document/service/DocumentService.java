package com.microbank.document.service;

import com.microbank.document.dto.event.TransactionEvent;
import com.microbank.document.dto.response.TransactionDocumentResponse;
import com.microbank.document.response.BaseApiResponse;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    // is being processed asynchronously in transaction-queue
    void createTransactionDocumentFromEvent(TransactionEvent event);

    // are being injected to the controller level to be utilized as endpoints
    BaseApiResponse<TransactionDocumentResponse> getTransactionDocumentById(UUID documentId);
    BaseApiResponse<TransactionDocumentResponse> getTransactionDocumentByTransactionId(UUID transactionId);
    BaseApiResponse<List<TransactionDocumentResponse>> getAllTransactionDocuments();

}
