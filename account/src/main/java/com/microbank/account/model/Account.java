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

    public Account() {}

    public Account(Long id, String IBAN, BigDecimal balance, String ownerName, Long userId) {
        this.id = id;
        this.IBAN = IBAN;
        this.balance = balance;
        this.ownerName = ownerName;
        this.userId = userId;
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

}
