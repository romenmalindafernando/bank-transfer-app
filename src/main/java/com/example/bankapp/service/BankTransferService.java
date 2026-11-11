package com.example.bankapp.service;

import com.example.bankapp.dto.TransferRequest;
import com.example.bankapp.model.BankTransaction;
import com.example.bankapp.model.Customer;
import com.example.bankapp.model.TransactionStatus;
import com.example.bankapp.repository.CustomerRepository;
import com.example.bankapp.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;

@Service
public class BankTransferService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public BankTransferService(
            CustomerRepository customerRepository,
            TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

@Transactional(rollbackFor = Exception.class)
public void transfer(TransferRequest request) throws Exception {

        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new IllegalArgumentException(
                    "Sender and receiver accounts must be different."
            );
        }

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero."
            );
        }

        Customer sender = customerRepository
                .findByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sender account not found."
                ));

        Customer receiver = customerRepository
                .findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Receiver account not found."
                ));

        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient account balance."
            );
        }

        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));

        customerRepository.save(sender);
        customerRepository.save(receiver);

        if (request.isSimulateFailure()) {
            throw new RuntimeException(
                    "Simulated failure! Transaction rolled back."
            );
        }

        BankTransaction transaction = BankTransaction.builder()
                .fromAccount(request.getFromAccount())
                .toAccount(request.getToAccount())
                .amount(request.getAmount())
                .status(TransactionStatus.SUCCESS)
                .description("Money transfer completed successfully")
                .build();

        transactionRepository.save(transaction);
    }
}

