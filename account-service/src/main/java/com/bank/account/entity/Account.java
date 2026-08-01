package com.bank.account.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 📍 Concept: "Spring Boot Backend" notebook — Entity Layer
 * BigDecimal use kiya balance ke liye — double/float mein rounding errors
 * hote hain jo banking mein financial loss ban sakte hain.
 *
 * @Version field optimistic locking deta hai — "ACID Transactions" notebook
 * ka concurrent-write problem yahan practically solve hota hai.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Version
    private Long version; // optimistic locking

    public Account() {}

    public Account(String accountNumber, BigDecimal balance, AccountType accountType, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.customer = customer;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Long getVersion() { return version; }
}
