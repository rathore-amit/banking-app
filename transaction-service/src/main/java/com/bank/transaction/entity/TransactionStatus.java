package com.bank.transaction.entity;

/** 📍 Concept: "Saga Pattern" notebook — Saga ke possible end-states */
public enum TransactionStatus {
    PENDING,
    DEBIT_DONE,
    COMPLETED,
    FAILED,
    COMPENSATED
}
