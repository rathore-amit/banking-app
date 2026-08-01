package com.bank.account.config;

import com.bank.account.repository.AccountRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * 📍 Concept: "Observability & Health Checks" notebook
 * GET /actuator/health mein ab "database" check bhi include hoga.
 * Kubernetes readinessProbe isi endpoint ko poll karke decide karta ki
 * traffic bhejni hai ya nahi.
 */
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final AccountRepository accountRepository;

    public DatabaseHealthIndicator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Health health() {
        try {
            accountRepository.count(); // simple query jo DB connectivity verify kare
            return Health.up().withDetail("database", "reachable").build();
        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
