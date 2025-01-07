package com.microbank.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record UpdateRoleRequest(
        @NotNull(message = "User ID cannot be empty")
        UUID userId,

        @NotNull(message = "New role field cannot be null")
        @Pattern(regexp = "^(ADMIN|USER)$", message = "Invalid role specified. Available roles: ADMIN, USER")
        String newRole
) {
}
