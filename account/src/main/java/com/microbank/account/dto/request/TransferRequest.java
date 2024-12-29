package com.microbank.account.dto.request;

import java.math.BigDecimal;

public record TransferRequest(
        String sourceIBAN,
        String targetIBAN,
        BigDecimal amount
) {
}
