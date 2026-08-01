package com.bank.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 📍 Concept: "Config Server" notebook
 *
 * Sabhi services (account-service, transaction-service, notification-service)
 * apni config yahan se startup pe fetch karti hain, instead of config
 * hardcode karne ke. Yahan hum "native" profile use kar rahe hain — config
 * files local filesystem (config-repo/) se serve hoti hain, taaki koi Git
 * repo setup na karna pade sirf try karne ke liye.
 *
 * Run karne ke baad dekho: http://localhost:8888/account-service/default
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
