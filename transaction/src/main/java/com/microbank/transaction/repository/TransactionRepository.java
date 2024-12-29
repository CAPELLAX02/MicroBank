package com.microbank.transaction.repository;

import com.microbank.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<List<Transaction>> findAllBySourceAccountId(Long accountId);
    Optional<List<Transaction>> findAllByTargetAccountId(Long accountId);

}
