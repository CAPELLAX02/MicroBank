package com.microbank.transaction.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEvent(
        String sourceAccountIBAN,
        String targetAccountIBAN,
        String sourceAccountEmail,
        String targetAccountEmail,
        String sourceName,
        String targetName,
        BigDecimal amount,
        LocalDateTime timestamp
) {
}
