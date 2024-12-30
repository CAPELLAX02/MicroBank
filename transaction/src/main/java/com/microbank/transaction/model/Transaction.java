package com.microbank.transaction.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name= "source_account_iban", nullable = false)
    private String sourceAccountIBAN;

    @Column(name= "target_account_iban", nullable = false)
    private String targetAccountIBAN;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 20)
    private String type;

    public Transaction() {}

    public Transaction(String sourceAccountIBAN, String targetAccountIBAN, BigDecimal amount, LocalDateTime timestamp, String type) {
        this.sourceAccountIBAN = sourceAccountIBAN;
        this.targetAccountIBAN = targetAccountIBAN;
        this.amount = amount;
        this.timestamp = timestamp;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSourceAccountIBAN() {
        return sourceAccountIBAN;
    }

    public void setSourceAccountIBAN(String sourceAccountIBAN) {
        this.sourceAccountIBAN = sourceAccountIBAN;
    }

    public String getTargetAccountIBAN() {
        return targetAccountIBAN;
    }

    public void setTargetAccountIBAN(String targetAccountIBAN) {
        this.targetAccountIBAN = targetAccountIBAN;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
