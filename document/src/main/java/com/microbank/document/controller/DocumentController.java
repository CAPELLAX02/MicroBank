package com.microbank.document.controller;

import com.microbank.document.dto.response.TransactionDocumentResponse;
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
    public ResponseEntity<TransactionDocumentResponse> getTransactionDocument(
            @PathVariable UUID documentId
    ) {
        return ResponseEntity.ok(documentService.getTransactionDocument(documentId));
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionDocumentResponse> getTransactionDocumentByTransactionId(
            @PathVariable UUID transactionId
    ) {
        return ResponseEntity.ok(documentService.getTransactionDocumentByTransactionId(transactionId));
    }

}
