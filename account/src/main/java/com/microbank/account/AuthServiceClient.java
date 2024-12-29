package com.microbank.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "auth-service",
        url = "http://localhost:8081/api/v1/auth"
)
public interface AuthServiceClient {

    @GetMapping("/{userId}")
    UserResponse getUserById(@Valid @PathVariable Long userId);

}
