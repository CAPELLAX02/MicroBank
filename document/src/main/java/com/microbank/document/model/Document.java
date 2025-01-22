package com.microbank.document.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID transactionId;

    @Column(nullable = false, length = 512)
    private String documentName;

    @Column(nullable = false, length = 2048)
    private String documentUrl;

    @Column(nullable = false)
    private String senderAccountIban;

    @Column(nullable = false)
    private String receiverAccountIban;

    @Column(nullable = false)
    private String senderAccountOwnerName;

    @Column(nullable = false)
    private String receiverAccountOwnerName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private String description;

    private LocalDateTime timestamp;

    public Document() {}

    public Document(
            UUID id,
            UUID transactionId,
            String documentName,
            String documentUrl,
            String senderAccountIban,
            String receiverAccountIban,
            String senderAccountOwnerName,
            String receiverAccountOwnerName,
            BigDecimal amount,
            String description,
            LocalDateTime timestamp
    ) {
        this.id = id;
        this.transactionId = transactionId;
        this.documentName = documentName;
        this.documentUrl = documentUrl;
        this.senderAccountIban = senderAccountIban;
        this.receiverAccountIban = receiverAccountIban;
        this.senderAccountOwnerName = senderAccountOwnerName;
        this.receiverAccountOwnerName = receiverAccountOwnerName;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getSenderAccountIban() {
        return senderAccountIban;
    }

    public void setSenderAccountIban(String senderAccountIban) {
        this.senderAccountIban = senderAccountIban;
    }

    public String getReceiverAccountIban() {
        return receiverAccountIban;
    }

    public void setReceiverAccountIban(String receiverAccountIban) {
        this.receiverAccountIban = receiverAccountIban;
    }

    public String getSenderAccountOwnerName() {
        return senderAccountOwnerName;
    }

    public void setSenderAccountOwnerName(String senderAccountOwnerName) {
        this.senderAccountOwnerName = senderAccountOwnerName;
    }

    public String getReceiverAccountOwnerName() {
        return receiverAccountOwnerName;
    }

    public void setReceiverAccountOwnerName(String receiverAccountOwnerName) {
        this.receiverAccountOwnerName = receiverAccountOwnerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
