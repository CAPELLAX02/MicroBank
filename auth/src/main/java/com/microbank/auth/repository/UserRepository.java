package com.microbank.auth.repository;

import com.microbank.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByActivationCode(String activationCode);

    Optional<User> findByKeycloakId(String keycloakId);

}