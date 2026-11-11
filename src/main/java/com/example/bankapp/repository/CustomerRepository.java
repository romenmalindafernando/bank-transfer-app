package com.example.bankapp.repository;

import com.example.bankapp.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    Optional<Customer> findByEmail(String email);
}