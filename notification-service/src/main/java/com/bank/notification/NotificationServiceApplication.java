package com.bank.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * NOTIFICATION SERVICE
 * =====================
 * 📍 Concept: "Message Broker & Event-Driven Communication" notebook
 * Ye service Transaction Service se "seedhe" connected nahi hai — sirf
 * Kafka topic ko sunti hai. Isse dono services fully decoupled hain:
 * Notification Service down/slow ho to Transaction Service pe koi asar
 * nahi padta (async, fire-and-forget).
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
