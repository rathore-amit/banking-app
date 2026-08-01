package com.bank.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 📍 Concept: "Spring Boot Backend" notebook — internal service-to-service contract.
 * Transaction Service isi shape ke saath Account Service ko debit/credit ke liye call karta hai.
 */
public class BalanceUpdateRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
