package com.microbank.transaction.feign;

import com.microbank.transaction.config.FeignConfig;
import com.microbank.transaction.dto.response.UserResponse;
import com.microbank.transaction.response.BaseApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "auth-service", url = "http://localhost:8081/api/v1/auth", configuration = FeignConfig.class)
public interface AuthServiceClient {

    @GetMapping("/users/me")
    BaseApiResponse<UserResponse> getCurrentUser();

    @GetMapping("/admin/users/{userID}")
    BaseApiResponse<UserResponse> getUserById(@PathVariable("userId") UUID userId);

}
