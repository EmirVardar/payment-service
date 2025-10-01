package com.example.payment_service.dto.callback;

import lombok.Data;

public record PaymentReturnForm(
        String md_status,           // 1 = başarılı
        String invoice_id,
        String order_id,
        String auth_code,
        String error,
        String status_code,
        String status_description,
        String amount,
        String installment,
        String hash_key
) {}
