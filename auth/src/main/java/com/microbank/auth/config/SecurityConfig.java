package com.microbank.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

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

                        .requestMatchers(HttpMethod.POST,  "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST,  "/api/v1/auth/activate").permitAll()
                        .requestMatchers(HttpMethod.POST,  "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST,  "/api/v1/auth/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.POST,  "/api/v1/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/auth/reset-password").permitAll()

                        .requestMatchers(HttpMethod.GET,   "/api/v1/auth/admin/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/v1/auth/admin/users/{userId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/auth/admin/users/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/auth/admin/users/access").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/auth/admin/users/{userId}").hasRole("ADMIN")

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
