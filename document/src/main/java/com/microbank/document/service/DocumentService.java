package com.microbank.document.service;

import com.microbank.document.dto.response.TransactionDocumentResponse;

public interface DocumentService {

    TransactionDocumentResponse createTransactionDocument(String transactionId);
    TransactionDocumentResponse getTransactionDocument(String documentId);

}
