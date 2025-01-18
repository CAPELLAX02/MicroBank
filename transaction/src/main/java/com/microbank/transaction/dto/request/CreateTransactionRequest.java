package com.microbank.transaction.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull(message = "Account ID cannot be empty")
        UUID sourceAccountId,

        @NotNull(message = "Target Account ID cannot be empty")
        UUID receiverAccountId,

        @NotNull(message = "Amount cannot be empty")
        BigDecimal amount,

        @Nullable
        String description
) {
}
