package com.microbank.transaction.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        LocalDateTime timestamp,
        String type
) {
}
