package com.microbank.transaction.dto.response;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String IBAN,
        String ownerName,
        BigDecimal balance,
        Long userId
) {
}
