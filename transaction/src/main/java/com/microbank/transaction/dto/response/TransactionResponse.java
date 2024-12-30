package com.microbank.transaction.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String sourceAccountIBAN,
        String targetAccountIBAN,
        BigDecimal amount,
        LocalDateTime timestamp,
        String type
) {
}
