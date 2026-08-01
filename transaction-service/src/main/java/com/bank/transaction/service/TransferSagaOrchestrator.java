package com.bank.transaction.service;

import com.bank.transaction.client.AccountServiceClient;
import com.bank.transaction.dto.TransferRequest;
import com.bank.transaction.dto.TransferResult;
import com.bank.transaction.entity.Transaction;
import com.bank.transaction.entity.TransactionStatus;
import com.bank.transaction.exception.TransferRejectedException;
import com.bank.transaction.messaging.TransactionEvent;
import com.bank.transaction.messaging.TransactionEventProducer;
import com.bank.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 📍📍📍 Concept: "Saga Pattern" notebook — poore project ka sabse important class 📍📍📍
 *
 * Ye Orchestration-based Saga hai — ek central class (ye) poore transfer
 * flow ko explicitly control karti hai:
 *
 *   1. Fraud/limit check (parallel, CompletableFuture se — "Multithreading" notebook)
 *   2. Debit source account (Account Service call, Circuit Breaker se protected)
 *   3. Credit destination account
 *   4. Agar step 3 fail ho -> COMPENSATE step 2 (refund the debit)
 *   5. Event publish karo Kafka pe (Notification Service ke liye)
 *
 * Har step DB mein Transaction record ke through track hota hai, taaki
 * kabhi bhi poori history/audit trail dekhi ja sake.
 */
@Service
public class TransferSagaOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(TransferSagaOrchestrator.class);

    private final AccountServiceClient accountServiceClient;
    private final FraudCheckService fraudCheckService;
    private final TransactionRepository transactionRepository;
    private final TransactionEventProducer eventProducer;

    public TransferSagaOrchestrator(AccountServiceClient accountServiceClient,
                                     FraudCheckService fraudCheckService,
                                     TransactionRepository transactionRepository,
                                     TransactionEventProducer eventProducer) {
        this.accountServiceClient = accountServiceClient;
        this.fraudCheckService = fraudCheckService;
        this.transactionRepository = transactionRepository;
        this.eventProducer = eventProducer;
    }

    public TransferResult executeTransfer(TransferRequest request) {
        Long from = request.getFromAccountId();
        Long to = request.getToAccountId();
        BigDecimal amount = request.getAmount();

        // Step 0: Transaction record banao PENDING state mein — audit trail ki shuruat
        Transaction txn = transactionRepository.save(new Transaction(from, to, amount, TransactionStatus.PENDING));
        logger.info("Saga started: txnId={}, from={}, to={}, amount={}", txn.getId(), from, to, amount);

        // Step 1: Fraud + daily limit check — parallel (CompletableFuture, "Multithreading" notebook)
        boolean isValid = fraudCheckService.validateTransfer(from, amount);
        if (!isValid) {
            txn.setStatus(TransactionStatus.FAILED);
            txn.setFailureReason("Failed fraud/limit check");
            transactionRepository.save(txn);
            publishEvent(txn);
            throw new TransferRejectedException("Transfer rejected: fraud check or daily limit exceeded");
        }

        // Step 2: DEBIT source account (Circuit Breaker protected call)
        try {
            accountServiceClient.debit(from, amount);
            txn.setStatus(TransactionStatus.DEBIT_DONE);
            transactionRepository.save(txn);
            logger.info("Saga step 2 done: debited {} from account {}", amount, from);
        } catch (Exception e) {
            txn.setStatus(TransactionStatus.FAILED);
            txn.setFailureReason("Debit failed: " + e.getMessage());
            transactionRepository.save(txn);
            publishEvent(txn);
            throw e; // GlobalExceptionHandler isko catch karega
        }

        // Step 3: CREDIT destination account
        try {
            accountServiceClient.credit(to, amount);
            txn.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(txn);
            logger.info("Saga step 3 done: credited {} to account {}. Transfer COMPLETE.", amount, to);
        } catch (Exception e) {
            // 📍 COMPENSATING TRANSACTION — Saga Pattern ka sabse important part
            logger.warn("Credit failed for txnId={}, running compensating transaction (refund debit)", txn.getId());
            accountServiceClient.compensateDebit(from, amount); // undo step 2

            txn.setStatus(TransactionStatus.COMPENSATED);
            txn.setFailureReason("Credit failed, debit was compensated: " + e.getMessage());
            transactionRepository.save(txn);
            publishEvent(txn);
            throw new TransferRejectedException("Transfer failed and was rolled back: " + e.getMessage());
        }

        publishEvent(txn);
        return TransferResult.success(txn.getId());
    }

    private void publishEvent(Transaction txn) {
        eventProducer.publish(new TransactionEvent(
                txn.getId(), txn.getFromAccountId(), txn.getToAccountId(), txn.getAmount(), txn.getStatus().name()));
    }
}
