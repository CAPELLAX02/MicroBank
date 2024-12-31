package com.microbank.transaction.repository;

import com.microbank.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<List<Transaction>> findAllBySourceAccountIBAN(String IBAN);

    List<Transaction> findAllBySourceAccountIBANIn(List<String> ibans);
}
