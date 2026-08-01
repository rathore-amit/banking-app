package com.bank.transaction.exception;

public class TransferRejectedException extends RuntimeException {
    public TransferRejectedException(String reason) {
        super(reason);
    }
}
