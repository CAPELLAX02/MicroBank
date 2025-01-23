package com.microbank.transaction.config;

import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtToken) {
                String token = jwtToken.getToken().getTokenValue();
                template.header("Authorization", "Bearer " + token);
            }
        };
    }

    @Bean
    public Request.Options feignRequestOptions() {
        return new Request.Options(
                5,
                TimeUnit.SECONDS,
                5,
                TimeUnit.SECONDS,
                true
        );    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(500, 2000, 3);
    }

}
