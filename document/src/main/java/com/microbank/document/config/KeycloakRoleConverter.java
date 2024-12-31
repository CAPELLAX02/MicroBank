package com.microbank.document.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        if (claims == null || !claims.containsKey("realm_access")) {
            return Collections.emptyList();
        }

        Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
        Collection<String> roles = (Collection<String>) realmAccess.getOrDefault("roles", Collections.emptyList());

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
