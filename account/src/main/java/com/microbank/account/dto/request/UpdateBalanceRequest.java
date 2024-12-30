package com.microbank.account.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateBalanceRequest(

        @NotBlank(message = "IBAN must be provided")
        @NotNull
        String accountIBAN,

        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        BigDecimal amount,

        @NotNull
        Boolean isDeposit

) {
}
