package com.microbank.document.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(name = "file_name", nullable = false, length = 512)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 2048)
    private String fileUrl;

    public Document() {}

    public Document(UUID id, String transactionId, String fileName, String fileUrl) {
        this.id = id;
        this.transactionId = transactionId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

}
