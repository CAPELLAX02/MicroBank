package com.microbank.auth.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateAccessRequest(
        @NotNull(message = "User ID cannot be empty")
        UUID userId,

        @NotNull(message = "isBanned field cannot be empty")
        boolean isBanned
) {
}
