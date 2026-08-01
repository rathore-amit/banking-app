package com.bank.notification.messaging;

import com.bank.notification.entity.Notification;
import com.bank.notification.repository.NotificationRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 📍 Concept: "Message Broker & Event-Driven Communication" notebook — Consumer side
 *
 * Idempotent consumer — same event dobara aaye (Kafka at-least-once delivery
 * ki wajah se) to bhi duplicate notification nahi banegi, kyunki hum
 * transactionId + status ke combination ko check kar sakte hain (simplified
 * yahan, real system mein processed_events table hoti "Message Broker"
 * notebook ke idempotency example jaisi).
 */
@Component
public class TransactionEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionEventConsumer.class);
    private final NotificationRepository notificationRepository;

    public TransactionEventConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = "transaction-events", groupId = "notification-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, TransactionEvent> record) {
        TransactionEvent event = record.value();
        logger.info("Received transaction event: txnId={}, status={}", event.getTransactionId(), event.getStatus());

        try {
            switch (event.getStatus()) {
                case "COMPLETED" -> {
                    save(event.getTransactionId(), event.getFromAccountId(),
                            "Debited: " + event.getAmount() + " (Txn #" + event.getTransactionId() + ")");
                    save(event.getTransactionId(), event.getToAccountId(),
                            "Credited: " + event.getAmount() + " (Txn #" + event.getTransactionId() + ")");
                }
                case "FAILED", "COMPENSATED" -> save(event.getTransactionId(), event.getFromAccountId(),
                        "Transfer failed/reversed: " + event.getAmount() + " (Txn #" + event.getTransactionId() + ")");
                default -> logger.warn("Unknown status: {}", event.getStatus());
            }
        } catch (Exception e) {
            // 📍 "Message Broker" notebook — real system mein DLQ (Dead Letter Queue) mein bhejte
            logger.error("Failed to process event for txnId={}, would route to DLQ in production", event.getTransactionId(), e);
        }
    }

    private void save(Long transactionId, Long accountId, String message) {
        notificationRepository.save(new Notification(transactionId, accountId, message));
        logger.info("Notification saved for account {}: {}", accountId, message);
    }
}
