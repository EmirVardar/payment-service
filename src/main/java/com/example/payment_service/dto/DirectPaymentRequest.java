package com.example.payment_service.dto;

public record DirectPaymentRequest(
        String ccHolderName,
        String ccNo,
        String expiryMonth,
        String expiryYear, // 4 hane
        String cvv,

        String currencyCode,
        Integer installmentsNumber, // 1
        String invoiceId,
        String invoiceDescription,
        String name,
        String surname,
        Double total,               // 2 ondalık gönder

        String merchantKey,

        String items,              // JSON string
        String cancelUrl,
        String returnUrl,

        String billEmail,
        String billPhone,

        String responseMethod,     // "POST"

        String hashKey // gerekirse boş geçilebilir
) {}
