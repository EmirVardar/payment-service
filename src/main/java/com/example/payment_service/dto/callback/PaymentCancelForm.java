package com.example.payment_service.dto.callback;

import lombok.Data;

public record PaymentCancelForm(
        String invoice_id,
        String order_id,
        String error,
        String status_code,
        String status_description,
        String amount,
        String installment,
        String hash_key
) {}
