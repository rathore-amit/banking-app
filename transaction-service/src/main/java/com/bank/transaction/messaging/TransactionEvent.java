package com.bank.transaction.messaging;

import java.io.Serializable;
import java.math.BigDecimal;

/** 📍 Concept: "Message Broker" notebook — event payload jo Kafka topic pe jaata hai */
public class TransactionEvent implements Serializable {
    private Long transactionId;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String status; // COMPLETED, FAILED, COMPENSATED

    public TransactionEvent() {}

    public TransactionEvent(Long transactionId, Long fromAccountId, Long toAccountId, BigDecimal amount, String status) {
        this.transactionId = transactionId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.status = status;
    }

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
