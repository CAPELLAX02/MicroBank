package com.microbank.account.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotNull
        Long userId,

        @NotNull
        BigDecimal initialBalance

) {
}
