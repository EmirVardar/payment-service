package com.example.payment_service.services;

import com.example.payment_service.dto.callback.PaymentReturnForm;
import com.example.payment_service.dto.callback.PaymentCancelForm;

public interface PaymentCallbackService {
    String handleReturn(PaymentReturnForm form);
    String handleCancel(PaymentCancelForm form);
}
