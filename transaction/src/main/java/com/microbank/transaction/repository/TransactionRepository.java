package com.microbank.transaction.repository;

import com.microbank.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findAllBySourceAccountId(UUID sourceAccountId);
    List<Transaction> findAllBySourceAccountIdIn(List<UUID> sourceAccountIds);

    List<Transaction> findAllBySourceAccountIdInOrReceiverAccountIdIn(
            List<UUID> sourceAccountIds,
            List<UUID> receiverAccountIds
    );

    List<Transaction> findAllBySourceAccountIdOrReceiverAccountId(
            UUID sourceAccountId,
            UUID receiverAccountId
    );

}
