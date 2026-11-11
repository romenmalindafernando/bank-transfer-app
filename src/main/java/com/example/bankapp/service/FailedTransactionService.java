package com.example.bankapp.service;

import com.example.bankapp.model.BankTransaction;
import com.example.bankapp.model.TransactionStatus;
import com.example.bankapp.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class FailedTransactionService {

    private final TransactionRepository transactionRepository;

    public FailedTransactionService(
            TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(
            String fromAccount,
            String toAccount,
            BigDecimal amount,
            String reason) {

        BankTransaction transaction = BankTransaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .status(TransactionStatus.FAILED)
                .description(reason)
                .build();

        transactionRepository.save(transaction);
    }
}