package com.microbank.account.dto.response;

import java.util.UUID;

public record MinimalAccountResponse(
        UUID id,
        String IBAN,
        String ownerName
) {
}
