package com.bank.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long transactionId;
    private Long accountId;
    private String message;
    private LocalDateTime sentAt = LocalDateTime.now();

    public Notification() {}

    public Notification(Long transactionId, Long accountId, String message) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.message = message;
    }

    public Long getId() { return id; }
    public Long getTransactionId() { return transactionId; }
    public Long getAccountId() { return accountId; }
    public String getMessage() { return message; }
    public LocalDateTime getSentAt() { return sentAt; }
}
