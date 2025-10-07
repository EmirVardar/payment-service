package com.example.payment_service.repos;

import com.example.payment_service.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByOrderId(String orderId);
    Optional<Transaction> findByOrderId(String orderId);
    boolean existsByInvoiceId(String invoiceId);
    java.util.Optional<Transaction> findByInvoiceId(String invoiceId);

}
