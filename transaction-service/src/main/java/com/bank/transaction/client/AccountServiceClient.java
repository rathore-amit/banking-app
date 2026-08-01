package com.bank.transaction.client;

import com.bank.transaction.dto.BalanceUpdateRequest;
import com.bank.transaction.exception.AccountServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

/**
 * 📍 Concept: "Circuit Breaker" notebook — poora implementation
 *
 * Ye class Account Service ko call karti hai debit/credit ke liye. Agar
 * Account Service baar-baar fail ho rahi hai, circuit "Open" ho jaata hai
 * aur future calls turant fail hoti hain (fail-fast), bina naya network
 * call kiye — jaisa notebook mein Closed/Open/Half-Open states explain
 * kiye the.
 *
 * @Retry bhi laga hai — transient (temporary) failures ko 2 baar retry
 * karta hai, exponential backoff ke saath, cascading retry-storm se
 * bachne ke liye Circuit Breaker ke andar (jaisa notebook mein "retry
 * storm" warning thi).
 */
@Component
public class AccountServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceClient.class);
    private final RestTemplate restTemplate;

    public AccountServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "debitFallback")
    @Retry(name = "accountService")
    public void debit(Long accountId, BigDecimal amount) {
        String url = "http://account-service/api/accounts/" + accountId + "/debit";
        try {
            restTemplate.postForEntity(url, new BalanceUpdateRequest(amount), Void.class);
        } catch (RestClientException e) {
            throw new AccountServiceUnavailableException("Debit call failed for account " + accountId, e);
        }
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "creditFallback")
    @Retry(name = "accountService")
    public void credit(Long accountId, BigDecimal amount) {
        String url = "http://account-service/api/accounts/" + accountId + "/credit";
        try {
            restTemplate.postForEntity(url, new BalanceUpdateRequest(amount), Void.class);
        } catch (RestClientException e) {
            throw new AccountServiceUnavailableException("Credit call failed for account " + accountId, e);
        }
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "compensateFallback")
    public void compensateDebit(Long accountId, BigDecimal amount) {
        String url = "http://account-service/api/accounts/" + accountId + "/compensate-debit";
        restTemplate.postForEntity(url, new BalanceUpdateRequest(amount), Void.class);
    }

    // 📍 Fallback methods — circuit Open ho ya sabhi retries fail ho jaayein, ye chalta hai
    // (koi network call nahi hoti, turant response milta hai — jaisa "fail fast" notebook mein tha)
    private void debitFallback(Long accountId, BigDecimal amount, Throwable t) {
        logger.error("Circuit open / debit failed for account {}: {}", accountId, t.getMessage());
        throw new AccountServiceUnavailableException("Account service unavailable for debit", t);
    }

    private void creditFallback(Long accountId, BigDecimal amount, Throwable t) {
        logger.error("Circuit open / credit failed for account {}: {}", accountId, t.getMessage());
        throw new AccountServiceUnavailableException("Account service unavailable for credit", t);
    }

    private void compensateFallback(Long accountId, BigDecimal amount, Throwable t) {
        // Compensation khud fail ho jaaye to ye ek critical alert hona chahiye (manual review queue)
        logger.error("CRITICAL: compensation failed for account {} amount {} — manual review needed! {}",
                accountId, amount, t.getMessage());
    }
}
