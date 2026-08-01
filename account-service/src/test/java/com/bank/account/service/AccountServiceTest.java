package com.bank.account.service;

import com.bank.account.entity.Account;
import com.bank.account.entity.AccountStatus;
import com.bank.account.entity.AccountType;
import com.bank.account.exception.InsufficientBalanceException;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 📍 Concept: "Testing Strategies" + "Spring Boot Backend" notebooks
 * Unit test — @Mock/@InjectMocks se, koi Spring context load nahi hota,
 * milliseconds mein chalta hai.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void debit_shouldThrowException_whenInsufficientBalance() {
        Account account = new Account("ACC001", new BigDecimal("100"), AccountType.SAVINGS, null);
        account.setStatus(AccountStatus.ACTIVE);
        account.setId(1L);

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(account));

        assertThrows(InsufficientBalanceException.class, () ->
                accountService.debit(1L, new BigDecimal("500"))); // 500 > 100 available

        verify(accountRepository, never()).save(any());
    }

    @Test
    void debit_shouldSucceed_whenSufficientBalance() {
        Account account = new Account("ACC001", new BigDecimal("1000"), AccountType.SAVINGS, null);
        account.setStatus(AccountStatus.ACTIVE);
        account.setId(1L);

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        accountService.debit(1L, new BigDecimal("300"));

        verify(accountRepository).save(account);
        assert account.getBalance().compareTo(new BigDecimal("700")) == 0;
    }
}
