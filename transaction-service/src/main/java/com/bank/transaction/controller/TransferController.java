package com.bank.transaction.controller;

import com.bank.transaction.dto.TransferRequest;
import com.bank.transaction.dto.TransferResult;
import com.bank.transaction.entity.Transaction;
import com.bank.transaction.repository.TransactionRepository;
import com.bank.transaction.service.TransferSagaOrchestrator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 📍 Concept: "Spring Boot Backend" notebook — REST Controller Layer */
@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferSagaOrchestrator sagaOrchestrator;
    private final TransactionRepository transactionRepository;

    public TransferController(TransferSagaOrchestrator sagaOrchestrator, TransactionRepository transactionRepository) {
        this.sagaOrchestrator = sagaOrchestrator;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping
    public ResponseEntity<TransferResult> transfer(@Valid @RequestBody TransferRequest request) {
        TransferResult result = sagaOrchestrator.executeTransfer(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        return transactionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getAccountHistory(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId));
    }
}
