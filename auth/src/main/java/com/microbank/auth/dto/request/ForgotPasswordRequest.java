package com.microbank.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(

        @NotBlank(message = "Email field cannot be blank")
        @Email(message = "Invalid email format")
        String email

) {
}
