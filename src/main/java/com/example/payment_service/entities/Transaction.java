package com.example.payment_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "transactions",
        uniqueConstraints = @UniqueConstraint(name = "uk_transactions_order_id", columnNames = "order_id")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id", nullable = false, length = 64)
    private String invoiceId;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "status", nullable = false, length = 16)
    private String status; // SUCCESS, FAILED, CANCELLED

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "installment")
    private Integer installment;

    @Column(name = "currency_code", length = 8)
    private String currencyCode;

    @Column(name = "auth_code", length = 64)
    private String authCode;

    @Column(name = "provider_status_code", length = 32)
    private String providerStatusCode;

    @Column(name = "provider_status_description", length = 256)
    private String providerStatusDescription;

    @Column(name = "error_message", length = 512)
    private String errorMessage;

    // Debug / denetim için tüm gelen formu saklamak istersen
    @Lob
    @Column(name = "raw_payload")
    private String rawPayload;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
