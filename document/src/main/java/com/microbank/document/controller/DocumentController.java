package com.microbank.document.controller;

import com.microbank.document.dto.response.TransactionDocumentResponse;
import com.microbank.document.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/{transactionId}")
    public ResponseEntity<TransactionDocumentResponse> createTransactionDocument(
            @PathVariable String transactionId
    ) {
        return ResponseEntity.ok(documentService.createTransactionDocument(transactionId));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<TransactionDocumentResponse> getTransactionDocument(
            @PathVariable String documentId
    ) {
        return ResponseEntity.ok(documentService.getTransactionDocument(documentId));
    }

}
