package com.microbank.notification.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionEvent(
        UUID transactionId,
        UUID senderAccountId,
        UUID receiverAccountId,
        String senderAccountEmail,
        String receiverAccountEmail,
        String senderAccountIban,
        String receiverAccountIban,
        String senderAccountOwnerName,
        String receiverAccountOwnerName,
        BigDecimal amount,
        String description,
        LocalDateTime timestamp
) implements Serializable {
}
