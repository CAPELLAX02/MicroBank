package com.microbank.transaction.dto.request;

import com.microbank.transaction.exceptions.CustomException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull(message = "Account ID cannot be empty")
        UUID senderAccountId,

//        @NotNull(message = "Target Account ID cannot be empty")
        @Nullable
        UUID receiverAccountId,

        @Nullable
        String receiverAccountIban,

        @NotNull(message = "Amount cannot be empty")
        BigDecimal amount,

        @Nullable
        String description
) {
        public void validate() {
                if (receiverAccountId == null && (receiverAccountIban == null || receiverAccountIban.isBlank())) {
                        throw new CustomException("Either IBAN or ID of the receiver account must be provided");
                }
        }
}
