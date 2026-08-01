package com.bank.transaction.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

/**
 * 📍 Concept: "Multithreading Basics" + "Concurrency in Spring Boot" notebooks
 *
 * Fraud check aur daily-limit check dono independent hain — CompletableFuture
 * se dono ko parallel chalate hain, phir combine karke decide karte hain.
 * Sequential hota to total time = fraudCheck + limitCheck; parallel mein
 * total time ≈ max(fraudCheck, limitCheck).
 */
@Service
public class FraudCheckService {

    @Value("${banking.transfer.fraud-check-threshold:50000}")
    private BigDecimal fraudCheckThreshold;

    @Value("${banking.transfer.max-daily-limit:100000}")
    private BigDecimal maxDailyLimit;

    public CompletableFuture<Boolean> isFraudulent(BigDecimal amount) {
        return CompletableFuture.supplyAsync(() -> {
            simulateExternalCallDelay(300); // simulate ek external fraud-check API call
            return amount.compareTo(fraudCheckThreshold) > 0;
        });
    }

    public CompletableFuture<Boolean> isWithinDailyLimit(Long accountId, BigDecimal amount) {
        return CompletableFuture.supplyAsync(() -> {
            simulateExternalCallDelay(200); // simulate DB/cache lookup for today's total
            return amount.compareTo(maxDailyLimit) <= 0;
        });
    }

    /**
     * 📍 thenCombine — dono independent CompletableFutures ko combine karna
     */
    public boolean validateTransfer(Long accountId, BigDecimal amount) {
        CompletableFuture<Boolean> fraudCheck = isFraudulent(amount);
        CompletableFuture<Boolean> limitCheck = isWithinDailyLimit(accountId, amount);

        return fraudCheck.thenCombine(limitCheck, (isFraud, withinLimit) -> !isFraud && withinLimit)
                .join(); // dono complete hone ka wait
    }

    private void simulateExternalCallDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
