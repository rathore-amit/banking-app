package com.bank.notification.messaging;

import java.io.Serializable;
import java.math.BigDecimal;

/** 📍 Producer (Transaction Service) jaisa hi shape — dono services independently maintain karte hain apna copy */
public class TransactionEvent implements Serializable {
    private Long transactionId;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String status;

    public Long getTransactionId() { return transactionId; }
    public Long getFromAccountId() { return fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
}
