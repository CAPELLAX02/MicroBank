package com.microbank.auth.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.config.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.config.realm}")
    private String keycloakRealm;

    @Value("${keycloak.config.username}")
    private String keycloakUsername;

    @Value("${keycloak.config.password}")
    private String keycloakPassword;

    @Value("${keycloak.config.client-id}")
    private String keycloakClientId;

    @Value("${keycloak.config.client-secret}")
    private String keycloakClientSecret;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(keycloakRealm)
                .grantType(OAuth2Constants.PASSWORD)
                .username(keycloakUsername)
                .password(keycloakPassword)
                .clientId(keycloakClientId)
                .clientSecret(keycloakClientSecret)
                .build();
    }

}
