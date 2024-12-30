package com.microbank.auth.dto.response;

public record UserResponse(
        Long id,
        String keycloakId,
        String firstName,
        String lastName,
        String username,
        String email
) {
}
