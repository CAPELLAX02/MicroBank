package com.microbank.account.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String IBAN;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String keycloakId;

    public Account() {}

    public Account(UUID id, String IBAN, BigDecimal balance, String ownerName, UUID userId, String keycloakId) {
        this.id = id;
        this.IBAN = IBAN;
        this.balance = balance;
        this.ownerName = ownerName;
        this.userId = userId;
        this.keycloakId = keycloakId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

}
