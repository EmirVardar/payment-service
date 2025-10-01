package com.example.payment_service.services;

import com.example.payment_service.dto.DirectPaymentRequest;

public interface PaymentService {
    String start3DPayment(DirectPaymentRequest req); // HTML döner
}
