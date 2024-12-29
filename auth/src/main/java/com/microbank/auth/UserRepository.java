package com.microbank.auth;

import com.microbank.auth.dto.response.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByActivationCode(String activationCode);

}
