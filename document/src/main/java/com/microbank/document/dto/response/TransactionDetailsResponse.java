package com.microbank.document.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDetailsResponse(
        String transactionId,
        String sourceAccountIBAN,
        String targetAccountIBAN,
        String sourceAccountOwnerName,
        String targetAccountOwnerName,
        BigDecimal amount,
        LocalDateTime timestamp
) {
}
