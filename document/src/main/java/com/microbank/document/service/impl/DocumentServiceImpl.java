package com.microbank.document.service.impl;

import com.microbank.document.dto.event.TransactionEvent;
import com.microbank.document.dto.response.TransactionDocumentResponse;
import com.microbank.document.model.Document;
import com.microbank.document.repository.DocumentRepository;
import com.microbank.document.service.DocumentService;
import com.microbank.document.service.MinIOService;
import com.microbank.document.service.utils.TransactionDocumentResponseBuilder;
import com.microbank.document.utils.PDFGenerator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final MinIOService minIOService;
    private final TransactionDocumentResponseBuilder transactionDocumentResponseBuilder;

    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            MinIOService minIOService,
            TransactionDocumentResponseBuilder transactionDocumentResponseBuilder
    ) {
        this.documentRepository = documentRepository;
        this.minIOService = minIOService;
        this.transactionDocumentResponseBuilder = transactionDocumentResponseBuilder;
    }

    @Override
    public void createTransactionDocumentFromEvent(TransactionEvent event) {
        ByteArrayInputStream pdfStream = PDFGenerator.generateTransactionDocument(event);
        String fileName = "TRANSACTION-" + event.transactionId() + ".pdf";
        String fileUrl = minIOService.uploadFile(fileName, pdfStream, "application/pdf");

        Document document = new Document();
        document.setTransactionId(event.transactionId());
        document.setDocumentName(fileName);
        document.setDocumentUrl(fileUrl);
        document.setSenderAccountIban(event.senderAccountIban());
        document.setReceiverAccountIban(event.receiverAccountIban());
        document.setSenderAccountOwnerName(event.senderAccountOwnerName());
        document.setReceiverAccountOwnerName(event.receiverAccountOwnerName());
        document.setAmount(event.amount());
        document.setDescription(event.description());
        document.setTimestamp(event.timestamp());

        documentRepository.save(document);
    }


    @Override
    public TransactionDocumentResponse getTransactionDocumentById(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found for ID: " + documentId));

        return transactionDocumentResponseBuilder.buildTransactionDocumentResponse(document);
    }

    @Override
    public TransactionDocumentResponse getTransactionDocumentByTransactionId(UUID transactionId) {
        Document document = documentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Document not found for transaction ID: " + transactionId));

        return transactionDocumentResponseBuilder.buildTransactionDocumentResponse(document);
    }

}
