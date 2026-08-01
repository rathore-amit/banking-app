package com.bank.transaction.dto;

import java.math.BigDecimal;

/** 📍 Account Service ke debit/credit endpoints ka same-shape request */
public class BalanceUpdateRequest {
    private BigDecimal amount;

    public BalanceUpdateRequest() {}
    public BalanceUpdateRequest(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
