package com.microbank.auth.dto.response;

import com.microbank.auth.model.enums.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String keycloakId,
        String username,
        String email,
        String firstName,
        String lastName,
        UserRole role,
        boolean isBanned
) {
}
