package com.microbank.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ActivationRequest(

        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Activation code cannot be blank")
        String activationCode

) {
}
