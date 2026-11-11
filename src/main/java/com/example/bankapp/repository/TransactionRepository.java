package com.example.bankapp.repository;

import com.example.bankapp.model.BankTransaction;
import com.example.bankapp.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<BankTransaction, Long> {

    @Query("""
        SELECT t FROM BankTransaction t
        WHERE t.fromAccount = :account
           OR t.toAccount = :account
        ORDER BY t.createdAt DESC
    """)
    List<BankTransaction> findAccountHistory(
            @Param("account") String account
    );

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM BankTransaction t
        WHERE t.fromAccount = :account
          AND t.status = :status
    """)
    BigDecimal calculateTotalSent(
            @Param("account") String account,
            @Param("status") TransactionStatus status
    );
}