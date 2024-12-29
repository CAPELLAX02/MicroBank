package com.microbank.account.repository;

import com.microbank.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByUserId(Long userId);
    Optional<Account> findByIBAN(String IBAN);

}
