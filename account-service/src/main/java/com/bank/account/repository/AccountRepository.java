package com.bank.account.repository;

import com.bank.account.entity.Account;
import com.bank.account.entity.AccountType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 📍 Concept: "Spring Boot Backend" notebook — Repository Layer (Spring Data JPA)
 * Sirf interface, koi implementation nahi — Spring runtime pe generate karta hai.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerIdAndAccountType(Long customerId, AccountType type);

    @Query("SELECT a FROM Account a WHERE a.balance > :minBalance")
    List<Account> findAccountsAboveBalance(@Param("minBalance") BigDecimal minBalance);

    // 📍 Pessimistic lock — "ACID Transactions" + "Spring Boot Backend" notebooks
    // Money transfer ke time race condition rokne ke liye — dusra transaction
    // tab tak wait karega jab tak ye transaction commit/rollback na ho jaaye
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);
}
