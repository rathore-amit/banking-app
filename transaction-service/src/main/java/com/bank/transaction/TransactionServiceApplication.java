package com.bank.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * TRANSACTION SERVICE
 * ====================
 * 📍 Concepts demonstrated:
 *  - Saga Pattern (Orchestration-based): debit -> reserve -> credit, with
 *    compensating transaction on failure
 *  - Circuit Breaker (Resilience4j): protects calls to Account Service
 *  - Message Broker (Kafka): publishes TransactionCompleted events
 *  - Multithreading (CompletableFuture): fraud check + limit check in parallel
 *  - Distributed Locking concept: avoided via Account Service's pessimistic lock
 */
@SpringBootApplication
@EnableDiscoveryClient
public class TransactionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }
}
