package com.microbank.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotNull
        Long userId,

        @NotBlank
        @NotNull
        String ownerName,

        @NotNull
        BigDecimal initialBalance

) {
}
