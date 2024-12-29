package com.microbank.auth.dto.response;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String username,
        String email
) {
}
