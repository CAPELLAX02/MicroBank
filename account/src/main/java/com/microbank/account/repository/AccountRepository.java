package com.microbank.account.repository;

import com.microbank.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findAllByUserId(UUID userId);
    Optional<Account> findByIBAN(String IBAN);

    Boolean existsAccountByKeycloakId(String keycloakId);
    List<Account> findAllByKeycloakId(String keycloakId);


}
