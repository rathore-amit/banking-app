package com.bank.account.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long accountId, BigDecimal amount) {
        super("Account " + accountId + " has insufficient balance for amount: " + amount);
    }
}
