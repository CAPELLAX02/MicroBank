package com.microbank.account.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String keycloakId,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
