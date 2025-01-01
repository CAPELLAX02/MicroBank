package com.microbank.document.service.impl;

import com.microbank.document.dto.response.TransactionDocumentResponse;
import com.microbank.document.feign.TransactionServiceClient;
import com.microbank.document.model.Document;
import com.microbank.document.repository.DocumentRepository;
import com.microbank.document.service.DocumentService;
import com.microbank.document.service.MinIOService;
import com.microbank.document.utils.PDFGenerator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final MinIOService minIOService;
    private final TransactionServiceClient transactionServiceClient;

    public DocumentServiceImpl(DocumentRepository documentRepository, MinIOService minIOService, TransactionServiceClient transactionServiceClient) {
        this.documentRepository = documentRepository;
        this.minIOService = minIOService;
        this.transactionServiceClient = transactionServiceClient;
    }

    @Override
    public TransactionDocumentResponse createTransactionDocument(String transactionId) {
        var transactionDetails = transactionServiceClient.getTransactionDetailsByTransactionId(transactionId);

        ByteArrayInputStream pdfStream = PDFGenerator.generateTransactionDocument(transactionDetails);

        String fileName = "TRANSACTION-" + transactionId + ".pdf";
        String fileUrl = minIOService.uploadFile(fileName, pdfStream, "application/pdf");

        Document document = new Document();
        document.setTransactionId(transactionId);
        document.setFileName(fileName);
        document.setFileUrl(fileUrl);

        documentRepository.save(document);

        return new TransactionDocumentResponse(
                fileUrl,
                transactionId,
                transactionDetails.sourceAccountIBAN(),
                transactionDetails.targetAccountIBAN(),
                transactionDetails.sourceAccountOwnerName(),
                transactionDetails.targetAccountOwnerName(),
                transactionDetails.amount(),
                transactionDetails.timestamp()
        );
    }

    @Override
    public TransactionDocumentResponse getTransactionDocument(String documentId) {
        Document document = documentRepository.findById(UUID.fromString(documentId))
                .orElseThrow(() -> new RuntimeException("Document not found for ID: " + documentId));

        return new TransactionDocumentResponse(
                document.getFileUrl(),
                document.getTransactionId(),
                null, // Source IBAN can be fetched if necessary
                null, // Target IBAN can be fetched if necessary
                null, // Source owner name can be fetched if necessary
                null, // Target owner name can be fetched if necessary
                null, // Amount can be fetched if necessary
                null  // Timestamp can be fetched if necessary
        );
    }
}
