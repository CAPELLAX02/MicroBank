package com.microbank.transaction.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotNull(message = "Source account ID is required")
        Long sourceAccountId,

        @NotNull(message = "Target account ID is required")
        Long targetAccountId,

        @NotNull(message = "Amount is required")
        @Min(value = 0, message = "Amount must be greater than zero")
        BigDecimal amount
) {
}
