package com.bank.account.service;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.entity.Account;
import com.bank.account.entity.AccountStatus;
import com.bank.account.entity.Customer;
import com.bank.account.exception.AccountNotFoundException;
import com.bank.account.exception.InsufficientBalanceException;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 📍 Concepts demonstrated here:
 *  - "Spring Boot Backend": Service layer, constructor DI, @Transactional
 *  - "Database Performance & Caching": @Cacheable with Redis
 *  - "Java Collections & Streams": filter/map/collect, ConcurrentHashMap
 *  - "ACID Transactions": pessimistic locking during debit/credit
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    // 📍 "Multithreading" notebook — ConcurrentHashMap safe hai multiple request-threads se
    // simultaneous access ke liye (in-memory idempotency-key tracking, demo purpose)
    private final Map<String, Boolean> processedIdempotencyKeys = new ConcurrentHashMap<>();

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Cacheable(value = "accounts", key = "#id") // 📍 Caching notebook — repeated reads DB hit nahi karte
    public AccountResponse getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return AccountResponse.fromEntity(account);
    }

    @Cacheable(value = "accountsByNumber", key = "#accountNumber")
    public AccountResponse getByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return AccountResponse.fromEntity(account);
    }

    @Transactional
    @CacheEvict(value = "accounts", allEntries = true) // naya account bana, purane cache invalidate karo
    public AccountResponse createAccount(CreateAccountRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new AccountNotFoundException(request.getCustomerId()));

        Account account = new Account(generateAccountNumber(), BigDecimal.ZERO, request.getAccountType(), customer);
        Account saved = accountRepository.save(account);
        return AccountResponse.fromEntity(saved);
    }

    public Page<AccountResponse> listAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable).map(AccountResponse::fromEntity);
    }

    // 📍 "Java Stream API" notebook — filter + map + collect ek saath
    public List<AccountResponse> getSavingsAccountsForCustomer(Long customerId) {
        return accountRepository.findByCustomerIdAndAccountType(customerId, com.bank.account.entity.AccountType.SAVINGS)
                .stream()
                .map(AccountResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 📍 "Java Stream API" notebook — reduce se total balance calculate karna
    public BigDecimal getTotalBalanceForCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new AccountNotFoundException(customerId))
                .getAccounts()
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 📍 "ACID Transactions" + "Spring Boot Backend" (@Transactional) notebooks
     * Transaction Service isko call karta hai transfer ke debit step ke liye.
     * Pessimistic lock se race condition rukti hai — jaisa notebook mein dekha.
     */
    @Transactional
    @CacheEvict(value = "accounts", key = "#accountId")
    public void debit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active: " + accountId);
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(accountId, amount);
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    @Transactional
    @CacheEvict(value = "accounts", key = "#accountId")
    public void credit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    /**
     * 📍 "Saga Pattern" notebook — compensating transaction.
     * Agar transfer ke doosre step (credit) mein failure ho, ye pehle step
     * (debit) ko undo karta hai.
     */
    @Transactional
    @CacheEvict(value = "accounts", key = "#accountId")
    public void compensateDebit(Long accountId, BigDecimal amount) {
        credit(accountId, amount); // reverse of debit is credit
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() + new Random().nextInt(1000);
    }
}
