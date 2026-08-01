package com.bank.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ACCOUNT SERVICE
 * =================
 * 📍 Concepts demonstrated in this service:
 *  - Spring Boot Backend notebook: Entity/Repository/Service/Controller layers, DI, DTOs,
 *    Exception Handling, Validation, Collections, Streams
 *  - Database Performance & Caching notebook: @Cacheable with Redis
 *  - Service Discovery notebook: registers itself with Eureka
 *  - Config Server notebook: fetches business config on startup
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
