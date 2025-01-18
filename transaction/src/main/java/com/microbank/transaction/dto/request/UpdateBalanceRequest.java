package com.microbank.transaction.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateBalanceRequest(

        @NotNull(message = "Account ID cannot be empty")
        UUID accountId,

        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        BigDecimal amount,

        @NotNull
        Boolean isDeposit

) {
}
