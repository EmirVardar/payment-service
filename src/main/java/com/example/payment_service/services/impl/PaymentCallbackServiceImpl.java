package com.example.payment_service.services.impl;

import com.example.payment_service.dto.callback.PaymentCancelForm;
import com.example.payment_service.dto.callback.PaymentReturnForm;
import com.example.payment_service.entities.Transaction;
import com.example.payment_service.mapper.TransactionMapper;     // 👈 EKLENDİ
import com.example.payment_service.repos.TransactionRepository;
import com.example.payment_service.services.PaymentCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCallbackServiceImpl implements PaymentCallbackService {

    private final TransactionRepository txRepo;
    private final TransactionMapper mapper;                     // 👈 EKLENDİ

    @Override
    public String handleReturn(PaymentReturnForm form) {
        boolean ok = "1".equals(form.md_status());
        String orderId = form.order_id();

        // 1) Aynı orderId daha önce işlendi mi? (idempotency)
        if (orderId != null && txRepo.existsByOrderId(orderId)) {
            return ok ? "PAYMENT_OK" : "PAYMENT_NOT_OK";
        }

        // 2) invoiceId ile mevcut kayıt var mı? -> GÜNCELLE
        var existing = txRepo.findByInvoiceId(form.invoice_id()).orElse(null);
        if (existing != null) {
            mapper.updateFromReturn(form, existing);           // 👈 mapper update
            txRepo.save(existing);
            return ok ? "PAYMENT_OK" : "PAYMENT_NOT_OK";
        }

        // 3) Yoksa edge-case -> YENİ oluştur
        Transaction tx = mapper.newFromReturn(form);           // 👈 mapper create
        txRepo.save(tx);
        return ok ? "PAYMENT_OK" : "PAYMENT_NOT_OK";
    }

    @Override
    public String handleCancel(PaymentCancelForm form) {
        String orderId = form.order_id();

        // 1) Aynı orderId daha önce işlendi mi?
        if (orderId != null && txRepo.existsByOrderId(orderId)) {
            return "PAYMENT_CANCELLED";
        }

        // 2) invoiceId ile mevcut kayıt var mı? -> GÜNCELLE
        var existing = txRepo.findByInvoiceId(form.invoice_id()).orElse(null);
        if (existing != null) {
            mapper.updateFromCancel(form, existing);           // 👈 mapper update
            txRepo.save(existing);
            return "PAYMENT_CANCELLED";
        }

        // 3) Yoksa edge-case -> YENİ oluştur
        Transaction tx = mapper.newFromCancel(form);           // 👈 mapper create
        txRepo.save(tx);
        return "PAYMENT_CANCELLED";
    }
}
