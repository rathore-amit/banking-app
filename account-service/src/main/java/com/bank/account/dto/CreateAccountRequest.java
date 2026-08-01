package com.bank.account.dto;

import com.bank.account.entity.AccountType;
import jakarta.validation.constraints.NotNull;

/**
 * 📍 Concept: "Spring Boot Backend" notebook — Validation
 * Client "balance" input nahi karta — hamesha server-side default se shuru hota hai.
 */
public class CreateAccountRequest {

    @NotNull(message = "customerId is required")
    private Long customerId;

    @NotNull(message = "accountType is required")
    private AccountType accountType;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
}
