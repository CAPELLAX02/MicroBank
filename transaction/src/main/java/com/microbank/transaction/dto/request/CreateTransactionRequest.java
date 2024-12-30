package com.microbank.transaction.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotNull(message = "Source account IBAN is required")
        String sourceAccountIBAN,

        @NotNull(message = "Target account IBAN is required")
        String targetAccountIBAN,

        @NotNull(message = "Amount is required")
        @Min(value = 0, message = "Amount must be greater than zero")
        BigDecimal amount
) {
}
