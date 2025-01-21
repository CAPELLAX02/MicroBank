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
    private boolean isBlocked;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private String ownerEmail;

    public Account() {}

    public Account(UUID id, String IBAN, BigDecimal balance, boolean isBlocked, String ownerName, UUID ownerId, String ownerEmail) {
        this.id = id;
        this.IBAN = IBAN;
        this.balance = balance;
        this.isBlocked = isBlocked;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.ownerEmail = ownerEmail;
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

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

}