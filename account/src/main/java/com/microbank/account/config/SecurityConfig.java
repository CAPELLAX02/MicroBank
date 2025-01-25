package com.microbank.account.config;

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
                        .requestMatchers(HttpMethod.POST,   "/api/v1/accounts").hasRole("USER")
//                        .requestMatchers(HttpMethod.PUT,    "/api/v1/accounts/accounts/balance").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/accounts/accounts/balance").permitAll()
                        .requestMatchers(HttpMethod.GET,    "/api/v1/accounts").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/accounts/{accountId}").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/accounts/{accountId}").hasRole("USER")

                        .requestMatchers(HttpMethod.GET,    "/api/v1/accounts/{accountId}/iban").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/accounts/iban/{iban}").hasRole("USER")

                        .requestMatchers(HttpMethod.GET,    "/api/v1/admin/accounts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/admin/accounts/{accountId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/admin/users/{userId}/accounts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,  "/api/v1/admin/accounts/{accountId}/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/accounts/{accountId}").hasRole("ADMIN")

                        // Feign Permissions
                        .requestMatchers(HttpMethod.GET,    "/api/v1/auth/users/me").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/auth/admin/users/{userId}").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }

}

