package com.microbank.transaction.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String IBAN,
        BigDecimal balance,
        boolean isBlocked,
        String ownerName,
        UUID ownerId,
        String ownerEmail
) {
}
