package com.microbank.document.repository;

import com.microbank.document.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Optional<Document> findByTransactionId(UUID transactionId);

}
