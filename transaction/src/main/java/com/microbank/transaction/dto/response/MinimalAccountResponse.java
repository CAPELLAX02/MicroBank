package com.microbank.transaction.dto.response;

import java.util.UUID;

public record MinimalAccountResponse(
        UUID id,
        String IBAN,
        String ownerName
) {
}
