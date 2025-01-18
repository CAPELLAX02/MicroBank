package com.microbank.account.feign;

import com.microbank.account.dto.response.UserResponse;
import com.microbank.account.response.BaseApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "auth-service", url = "http://localhost:8081/api/v1/auth")
public interface AuthServiceClient {

    @GetMapping("/users/me")
    BaseApiResponse<UserResponse> getCurrentUser();

    @GetMapping("/admin/users/{userId}")
    BaseApiResponse<UserResponse> getUserById(@PathVariable UUID userId);

}