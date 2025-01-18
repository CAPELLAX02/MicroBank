package com.microbank.transaction.dto.event;

import com.microbank.transaction.model.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequestedEvent(
        UUID transactionId,
        UUID senderAccountId,
        UUID receiverAccountId,
        BigDecimal amount,
        String description,
        TransactionStatus status
) {
}
