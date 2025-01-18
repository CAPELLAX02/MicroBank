package com.microbank.transaction.dto.event;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionCompletedEvent(
        UUID transactionId,
        UUID receiverAccountId,
        BigDecimal amount,
        String description
) {
}
