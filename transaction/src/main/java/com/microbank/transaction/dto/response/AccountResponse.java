package com.microbank.transaction.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String IBAN,
        String ownerName,
        BigDecimal balance,
        UUID userId
) {
}
