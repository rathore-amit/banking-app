package com.bank.account.dto;

import com.bank.account.entity.Account;
import com.bank.account.entity.AccountStatus;
import com.bank.account.entity.AccountType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 📍 Concept: "Spring Boot Backend" notebook — DTO Pattern
 * Entity ko directly expose nahi karte — "version" jaisa internal field
 * yahan expose nahi hota, aur API contract Entity se decoupled rehta hai.
 * Serializable — Redis cache mein store hone ke liye zaroori hai.
 */
public class AccountResponse implements Serializable {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    private AccountStatus status;
    private String customerName;

    public AccountResponse() {}

    public static AccountResponse fromEntity(Account account) {
        AccountResponse dto = new AccountResponse();
        dto.id = account.getId();
        dto.accountNumber = account.getAccountNumber();
        dto.balance = account.getBalance();
        dto.accountType = account.getAccountType();
        dto.status = account.getStatus();
        dto.customerName = account.getCustomer() != null ? account.getCustomer().getName() : null;
        return dto;
    }

    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public AccountType getAccountType() { return accountType; }
    public AccountStatus getStatus() { return status; }
    public String getCustomerName() { return customerName; }
    public void setId(Long id) { this.id = id; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
