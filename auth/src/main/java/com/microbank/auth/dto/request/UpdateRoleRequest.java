package com.microbank.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRoleRequest(
        @NotBlank(message = "New role field cannot be blank")
        String newRole
) {
}
