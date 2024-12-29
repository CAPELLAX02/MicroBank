package com.microbank.account;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String IBAN,
        String ownerName,
        BigDecimal balance,
        Long userId
) {
}
