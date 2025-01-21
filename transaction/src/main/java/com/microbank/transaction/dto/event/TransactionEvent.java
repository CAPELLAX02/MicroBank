package com.microbank.transaction.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionEvent(
        UUID transactionId,
        UUID senderAccountId,
        UUID receiverAccountId,
        String senderAccountIban,
        String receiverAccountIban,
        BigDecimal amount,
        String description,
        LocalDateTime timestamp
) {
}
