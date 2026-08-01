package com.bank.transaction.service;

import com.bank.transaction.client.AccountServiceClient;
import com.bank.transaction.dto.TransferRequest;
import com.bank.transaction.entity.Transaction;
import com.bank.transaction.entity.TransactionStatus;
import com.bank.transaction.messaging.TransactionEventProducer;
import com.bank.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 📍 Concept: "Testing Strategies" notebook — sabse critical scenario test:
 * agar credit fail ho, compensating transaction (refund) chalni chahiye.
 */
@ExtendWith(MockitoExtension.class)
class TransferSagaOrchestratorTest {

    @Mock private AccountServiceClient accountServiceClient;
    @Mock private FraudCheckService fraudCheckService;
    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionEventProducer eventProducer;

    @Test
    void executeTransfer_shouldCompensateDebit_whenCreditFails() {
        TransferSagaOrchestrator saga = new TransferSagaOrchestrator(
                accountServiceClient, fraudCheckService, transactionRepository, eventProducer);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("500"));

        Transaction savedTxn = new Transaction(1L, 2L, new BigDecimal("500"), TransactionStatus.PENDING);
        savedTxn.setId(100L);
        when(transactionRepository.save(any())).thenReturn(savedTxn);

        when(fraudCheckService.validateTransfer(eq(1L), any())).thenReturn(true); // fraud check pass
        doNothing().when(accountServiceClient).debit(eq(1L), any());              // debit success
        doThrow(new RuntimeException("Account 2 frozen"))
                .when(accountServiceClient).credit(eq(2L), any());                // credit FAILS

        assertThrows(RuntimeException.class, () -> saga.executeTransfer(request));

        // 🎯 Sabse important assertion — compensating transaction chalni chahiye
        verify(accountServiceClient, times(1)).compensateDebit(eq(1L), eq(new BigDecimal("500")));

        // Final status COMPENSATED save hua ho
        ArgumentCaptor<Transaction> txnCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, atLeastOnce()).save(txnCaptor.capture());
        Transaction lastSaved = txnCaptor.getAllValues().get(txnCaptor.getAllValues().size() - 1);
        assert lastSaved.getStatus() == TransactionStatus.COMPENSATED;
    }
}
