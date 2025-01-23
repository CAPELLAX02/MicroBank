package com.microbank.transaction.feign;

import com.microbank.transaction.config.FeignConfig;
import com.microbank.transaction.dto.response.UserResponse;
import com.microbank.transaction.feign.fallback.AuthServiceClientFallback;
import com.microbank.transaction.response.BaseApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "auth-service",
        url = "http://localhost:8081/api/v1/auth",
        configuration = FeignConfig.class,
        fallback = AuthServiceClientFallback.class
)
public interface AuthServiceClient {

    @GetMapping("/users/me")
    BaseApiResponse<UserResponse> getCurrentUser();

}
