package com.microbank.transaction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/transactions").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/transactions/me/{transactionId}").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/transactions/me").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/transactions/me/accounts/{accountId}").hasRole("USER")

                        .requestMatchers(HttpMethod.GET,  "/api/v1/transactions/admin/transactions").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/transactions/admin/transactions/{transactionId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/transactions/admin/accounts/{accountId}/transactions").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/transactions/admin/users/{userId}/transactions").hasRole("ADMIN")

                        // Feign Permissions
                        .requestMatchers(HttpMethod.PUT,  "/api/v1/accounts/balance").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/accounts/{accountId}").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/accounts/minimal/{accountId}").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/accounts/iban/{iban}").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,  "/api/v1/accounts/admin/users/{userId}").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }

}
