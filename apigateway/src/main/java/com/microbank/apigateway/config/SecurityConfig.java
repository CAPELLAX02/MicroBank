package com.microbank.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        return serverHttpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.POST,  "/api/v1/auth/register").permitAll()
                        .pathMatchers(HttpMethod.POST,  "/api/v1/auth/activate").permitAll()
                        .pathMatchers(HttpMethod.POST,  "/api/v1/auth/login").permitAll()
                        .pathMatchers(HttpMethod.POST,  "/api/v1/auth/refresh-token").permitAll()
                        .pathMatchers(HttpMethod.POST,  "/api/v1/auth/forgot-password").permitAll()
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/auth/reset-password").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                        jwt.jwtDecoder(ReactiveJwtDecoders.fromIssuerLocation(issuerUri))
                ))
                .build();
    }

}
