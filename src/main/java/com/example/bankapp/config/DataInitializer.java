package com.example.bankapp.config;

import com.example.bankapp.model.Customer;
import com.example.bankapp.repository.CustomerRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeCustomers(CustomerRepository repository) {

        return args -> {

            if (!repository.existsByAccountNumber("100001")) {
                repository.save(Customer.builder()
                        .accountNumber("100001")
                        .fullName("Kamal")
                        .email("kamal@example.com")
                        .balance(new BigDecimal("50000.00"))
                        .build());
            }

            if (!repository.existsByAccountNumber("100002")) {
                repository.save(Customer.builder()
                        .accountNumber("100002")
                        .fullName("Nimali")
                        .email("nimali@example.com")
                        .balance(new BigDecimal("25000.00"))
                        .build());
            }

            if (!repository.existsByAccountNumber("100003")) {
                repository.save(Customer.builder()
                        .accountNumber("100003")
                        .fullName("Saman")
                        .email("saman@example.com")
                        .balance(new BigDecimal("10000.00"))
                        .build());
            }

            System.out.println("Initial customer accounts loaded.");
        };
    }
}