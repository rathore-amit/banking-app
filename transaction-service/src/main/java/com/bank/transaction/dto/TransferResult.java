package com.bank.transaction.dto;

public class TransferResult {
    private Long transactionId;
    private String status;
    private String message;

    public static TransferResult success(Long transactionId) {
        TransferResult r = new TransferResult();
        r.transactionId = transactionId;
        r.status = "COMPLETED";
        r.message = "Transfer successful";
        return r;
    }

    public static TransferResult rejected(String reason) {
        TransferResult r = new TransferResult();
        r.status = "REJECTED";
        r.message = reason;
        return r;
    }

    public static TransferResult failed(String reason) {
        TransferResult r = new TransferResult();
        r.status = "FAILED";
        r.message = reason;
        return r;
    }

    public Long getTransactionId() { return transactionId; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
