package com.bank.transaction.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 📍 Concept: "Load Balancing & Rate Limiting" notebook — Client-side discovery
 * @LoadBalanced se "http://account-service/..." likhne par Spring Cloud
 * Eureka se resolve karke, agar multiple instances hon to unme round-robin
 * load balance karta hai — koi hardcoded IP:port nahi.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
