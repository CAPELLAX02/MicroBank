package com.microbank.auth;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:9098")
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .username("admin")
                .password("admin")
                .clientId("admin-cli")
//                .clientSecret("clientSecret")
                .build();
    }

}
