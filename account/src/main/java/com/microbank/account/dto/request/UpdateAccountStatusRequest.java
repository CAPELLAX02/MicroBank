package com.microbank.account.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateAccountStatusRequest(

        @NotNull(message = "Account ID cannot be empty")
        UUID accountId,

        @NotNull
        boolean isBlocked

) {
}
