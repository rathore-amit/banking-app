package com.bank.account.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
