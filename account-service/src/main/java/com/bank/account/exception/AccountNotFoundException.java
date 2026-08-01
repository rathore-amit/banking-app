package com.bank.account.exception;

/** 📍 Concept: "Spring Boot Backend" notebook — Exception Handling */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long accountId) {
        super("Account not found: " + accountId);
    }
    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }
}
