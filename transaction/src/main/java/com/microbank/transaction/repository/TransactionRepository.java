package com.microbank.transaction.repository;

import com.microbank.transaction.model.Transaction;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findAllBySourceAccountId(UUID sourceAccountId);
    List<Transaction> findAllBySourceAccountIdIn(List<UUID> sourceAccountIds);

    List<Transaction> findAllByReceiverAccountId(UUID receiverAccountId);
    List<Transaction> findAllByReceiverAccountIdIn(List<UUID> receiverAccountIds);

    List<Transaction> findAllBySourceAccountIdInOrReceiverAccountIdIn(List<UUID> accountIds, List<UUID> accountIds1);

    @Modifying
    @Query("UPDATE Transaction t SET t.status = :status WHERE t.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") String status);

}
