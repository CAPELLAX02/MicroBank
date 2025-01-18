package com.microbank.account.dto.response;

import com.microbank.account.model.UserRole;

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
