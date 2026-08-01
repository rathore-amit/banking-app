package com.bank.transaction.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 📍 Concept: "Message Broker & Event-Driven Communication" notebook
 *
 * Transfer complete/fail hone ke baad, Notification Service ko turant call
 * karne ki bajaye, hum ek event publish karte hain — Notification Service
 * apni speed se isse consume karke SMS/email bhejegi. Transaction Service
 * ko Notification Service ke down/slow hone ki chinta nahi karni padti
 * (decoupling).
 */
@Component
public class TransactionEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionEventProducer.class);
    private static final String TOPIC = "transaction-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransactionEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(TransactionEvent event) {
        // key = transactionId.toString() -> same transaction ke events hamesha same partition mein,
        // order guarantee milta hai (jaisa "Message Broker" notebook mein partition key dekha)
        kafkaTemplate.send(TOPIC, event.getTransactionId().toString(), event);
        logger.info("Published transaction event: txnId={}, status={}", event.getTransactionId(), event.getStatus());
    }
}
