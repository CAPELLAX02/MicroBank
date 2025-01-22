package com.microbank.document.dto.response;

import java.util.UUID;

public record TransactionDocumentResponse(
        String documentUrl,
        String documentName,
        UUID transactionId
) {
}
