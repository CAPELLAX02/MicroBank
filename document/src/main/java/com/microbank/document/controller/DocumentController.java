package com.microbank.document.controller;

import com.microbank.document.dto.response.TransactionDocumentResponse;
import com.microbank.document.response.BaseApiResponse;
import com.microbank.document.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<TransactionDocumentResponse>>> getAllTransactionDocuments() {
        return ResponseEntity.ok(documentService.getAllTransactionDocuments());
    }

    // TODO: Add endpoint for admin users and implement the RBAC also in the document microservice.
    // TODO: Implement the forgot-password and reset-password endpoints in the authentication microservice.
    // TODO: Strengthen the user CRUD operations in the auth microservice. PostgreSQL and Keycloak must be consistent.

    // TODO: Somehow implement IBAN <-> accountId conversion due to client convenience

    // TODO: Clear the whole project codes up a bit.
    // TODO: Complete the API Documentation after all.

}
