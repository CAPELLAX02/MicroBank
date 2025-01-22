package com.microbank.document.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionDocumentResponse(
        String documentUrl,
        String documentName,
        UUID transactionId,
        String senderAccountIban,
        String receiverAccountIban,
        String senderOwnerName,
        String receiverOwnerName,
        BigDecimal amount,
        String description,
        LocalDateTime timestamp
) {
}
