package com.bank.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 📍 Concept: "API Gateway" notebook
 *
 * System ka single entry point — Client kabhi bhi seedhe Account/Transaction/
 * Notification service ko call nahi karta, hamesha yahan se hokar jaata hai.
 * Yahan JWT validation, rate limiting, aur routing (Eureka se service discovery
 * use karke) sab hote hain.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
