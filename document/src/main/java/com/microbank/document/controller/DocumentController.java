package com.microbank.document.controller;

import com.microbank.document.dto.response.TransactionDocumentResponse;
import com.microbank.document.response.BaseApiResponse;
import com.microbank.document.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<BaseApiResponse<TransactionDocumentResponse>> getTransactionDocumentById(
            @PathVariable UUID documentId
    ) {
        return ResponseEntity.ok(documentService.getTransactionDocumentById(documentId));
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<BaseApiResponse<TransactionDocumentResponse>> getTransactionDocumentByTransactionId(
            @PathVariable UUID transactionId
    ) {
        return ResponseEntity.ok(documentService.getTransactionDocumentByTransactionId(transactionId));
    }

    // TODO: Add endpoint for admin users.
    // TODO: Make the API responses of the document microservice generic as in the other microservices.
    // TODO: Implement the forgot-password and reset-password endpoints in the authentication microservice.
    // TODO: Clear the whole project codes up a bit.
    // TODO: Complete the API Documentation after all.

}
