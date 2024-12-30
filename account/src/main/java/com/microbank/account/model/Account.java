package com.microbank.account.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String IBAN;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String keycloakId;

    public Account() {}

    public Account(Long id, String IBAN, BigDecimal balance, String ownerName, Long userId, String keycloakId) {
        this.id = id;
        this.IBAN = IBAN;
        this.balance = balance;
        this.ownerName = ownerName;
        this.userId = userId;
        this.keycloakId = keycloakId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

}
