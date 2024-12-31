package com.microbank.document.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

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

}
