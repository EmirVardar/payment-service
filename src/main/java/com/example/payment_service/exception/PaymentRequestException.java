package com.example.payment_service.exception;

public class PaymentRequestException extends RuntimeException {

    public PaymentRequestException(String message) {
        super(message);
    }

}
