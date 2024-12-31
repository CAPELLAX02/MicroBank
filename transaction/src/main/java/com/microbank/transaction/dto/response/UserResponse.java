package com.microbank.transaction.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String keycloakId,
        String firstName,
        String lastName,
        String username,
        String email
) {
}
