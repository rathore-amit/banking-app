package com.bank.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 📍 Concept: "Service Discovery & Registry" notebook
 *
 * Har microservice (Account, Transaction, Notification) is server ke saath
 * register hoti hai on startup. API Gateway aur services ek-doosre ko naam
 * se dhoondh sakte hain (jaise "ACCOUNT-SERVICE"), hardcoded IP/port ke bina.
 *
 * Run karne ke baad dekho: http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
