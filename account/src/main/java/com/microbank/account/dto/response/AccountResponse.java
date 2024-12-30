package com.microbank.account.dto.response;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        String IBAN,
        String ownerName,
        BigDecimal balance,
        UUID userId,
        @Nullable String email
) {
}
