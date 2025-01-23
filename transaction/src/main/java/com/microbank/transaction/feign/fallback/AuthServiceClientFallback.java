package com.microbank.transaction.feign.fallback;

import com.microbank.transaction.dto.response.UserResponse;
import com.microbank.transaction.feign.AuthServiceClient;
import com.microbank.transaction.response.BaseApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceClientFallback implements AuthServiceClient {

    @Override
    public BaseApiResponse<UserResponse> getCurrentUser() {
        return new BaseApiResponse<>(HttpStatus.SERVICE_UNAVAILABLE.value(), "Authentication service is temporarily unavailable.", null);
    }

}
