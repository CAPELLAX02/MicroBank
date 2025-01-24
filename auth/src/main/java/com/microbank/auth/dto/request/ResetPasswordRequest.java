package com.microbank.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(message = "Email field cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password recovery code cannot be blank")
        String passwordRecoveryCode,

        @NotBlank(message = "New password cannot be blank")
        @Size(min = 6,  message = "New password must be at least 6 characters")
        String newPassword

) {
}
