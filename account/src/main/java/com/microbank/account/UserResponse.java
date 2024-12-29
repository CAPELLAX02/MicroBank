package com.microbank.account;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String username,
        String email
) {
}
