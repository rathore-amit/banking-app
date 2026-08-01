package com.bank.account.controller;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.BalanceUpdateRequest;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 📍 Concept: "Spring Boot Backend" notebook — REST Controller Layer
 * + "HTTP Methods, Query Params" notebook — proper verb usage (GET/POST/PATCH)
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @GetMapping("/by-number/{accountNumber}")
    public ResponseEntity<AccountResponse> getByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getByAccountNumber(accountNumber));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<AccountResponse>> listAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(accountService.listAccounts(PageRequest.of(page, size)));
    }

    @GetMapping("/customer/{customerId}/savings")
    public ResponseEntity<List<AccountResponse>> getSavingsAccounts(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getSavingsAccountsForCustomer(customerId));
    }

    @GetMapping("/customer/{customerId}/total-balance")
    public ResponseEntity<BigDecimal> getTotalBalance(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getTotalBalanceForCustomer(customerId));
    }

    /**
     * 📍 Internal API — sirf Transaction Service (Saga participant) isko call karta hai.
     * Production mein ye endpoint internal-only network policy se protect hota
     * (jaisa "Container Orchestration" notebook mein Network Policy dekhi thi).
     */
    @PostMapping("/{id}/debit")
    public ResponseEntity<Void> debit(@PathVariable Long id, @Valid @RequestBody BalanceUpdateRequest request) {
        accountService.debit(id, request.getAmount());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<Void> credit(@PathVariable Long id, @Valid @RequestBody BalanceUpdateRequest request) {
        accountService.credit(id, request.getAmount());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/compensate-debit")
    public ResponseEntity<Void> compensateDebit(@PathVariable Long id, @Valid @RequestBody BalanceUpdateRequest request) {
        accountService.compensateDebit(id, request.getAmount());
        return ResponseEntity.noContent().build();
    }
}
