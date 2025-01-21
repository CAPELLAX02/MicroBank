package com.microbank.transaction.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID senderAccountId,
        UUID receiverAccountId,
        BigDecimal amount,
        String description
) {
}
