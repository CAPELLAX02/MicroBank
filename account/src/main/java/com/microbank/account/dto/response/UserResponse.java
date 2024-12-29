package com.microbank.account.dto.response;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String username,
        String email
) {
}
