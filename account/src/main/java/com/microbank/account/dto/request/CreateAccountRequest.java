package com.microbank.account.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @DecimalMin(value = "0.01", message = "Initial balance must be at least 0.01")
        BigDecimal initialBalance

) {
}
