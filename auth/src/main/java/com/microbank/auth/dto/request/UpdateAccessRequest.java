package com.microbank.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccessRequest(
        @NotBlank(message = "isBanned field cannot be blank")
        boolean isBanned
) {
}
