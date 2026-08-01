package com.bank.transaction.exception;

/** 📍 Concept: "Circuit Breaker" notebook — jab Account Service unreachable ho */
public class AccountServiceUnavailableException extends RuntimeException {
    public AccountServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
