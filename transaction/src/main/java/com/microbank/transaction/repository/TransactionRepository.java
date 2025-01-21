package com.microbank.transaction.repository;

import com.microbank.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findAllBySenderAccountId(UUID senderAccountId);
    List<Transaction> findAllBySenderAccountIdIn(List<UUID> senderAccountIds);

    List<Transaction> findAllBySenderAccountIdInOrReceiverAccountIdIn(
            List<UUID> senderAccountId,
            List<UUID> receiverAccountIds
    );

    List<Transaction> findAllBySenderAccountIdOrReceiverAccountId(
            UUID senderAccountId,
            UUID receiverAccountId
    );

}
