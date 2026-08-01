package com.bank.transaction.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 📍 Concept: "Spring Boot Backend" notebook — Validation
 */
public class TransferRequest {

    @NotNull(message = "fromAccountId is required")
    private Long fromAccountId;

    @NotNull(message = "toAccountId is required")
    private Long toAccountId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;

    @AssertTrue(message = "fromAccountId and toAccountId must be different")
    private boolean isValidTransfer() {
        return !Objects.equals(fromAccountId, toAccountId);
    }

    public Long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
