package com.example.payment_service.services.impl;

import com.example.payment_service.dto.callback.PaymentCancelForm;
import com.example.payment_service.dto.callback.PaymentReturnForm;
import com.example.payment_service.entities.Transaction;
import com.example.payment_service.repos.TransactionRepository;
import com.example.payment_service.services.PaymentCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCallbackServiceImpl implements PaymentCallbackService {

    private final TransactionRepository txRepo;

    @Override
    public String handleReturn(PaymentReturnForm form) {
        boolean ok = "1".equals(form.md_status());
        String orderId = form.order_id();

        // Zaten işlendi mi? (orderId bazlı)
        if (orderId != null && txRepo.existsByOrderId(orderId)) {
            return ok ? "PAYMENT_OK" : "PAYMENT_NOT_OK";
        }

        // Önceden açılmış PENDING var mı? (invoiceId bazlı)
        var existing = txRepo.findByInvoiceId(form.invoice_id()).orElse(null);
        if (existing != null) {
            existing.setOrderId(orderId);
            existing.setStatus(ok ? "SUCCESS" : "FAILED");
            existing.setAmount(toBigDecimal(form.amount()));
            existing.setInstallment(toInteger(form.installment()));
            existing.setAuthCode(form.auth_code());
            existing.setProviderStatusCode(form.status_code());
            existing.setProviderStatusDescription(form.status_description());
            existing.setErrorMessage(form.error());
            existing.setRawPayload(form.toString());
            txRepo.save(existing);
            return ok ? "PAYMENT_OK" : "PAYMENT_NOT_OK";
        }

        // Hiç yoksa (edge case) → yeni oluştur (user null kalabilir)
        var tx = Transaction.builder()
                .invoiceId(form.invoice_id())
                .orderId(orderId)
                .status(ok ? "SUCCESS" : "FAILED")
                .amount(toBigDecimal(form.amount()))
                .installment(toInteger(form.installment()))
                .authCode(form.auth_code())
                .providerStatusCode(form.status_code())
                .providerStatusDescription(form.status_description())
                .errorMessage(form.error())
                .rawPayload(form.toString())
                .build();
        txRepo.save(tx);
        return ok ? "PAYMENT_OK" : "PAYMENT_NOT_OK";
    }

    @Override
    public String handleCancel(PaymentCancelForm form) {
        String orderId = form.order_id();

        if (orderId != null && txRepo.existsByOrderId(orderId)) {
            return "PAYMENT_CANCELLED";
        }

        var existing = txRepo.findByInvoiceId(form.invoice_id()).orElse(null);
        if (existing != null) {
            existing.setOrderId(orderId);
            existing.setStatus("CANCELLED");
            existing.setAmount(toBigDecimal(form.amount()));
            existing.setInstallment(toInteger(form.installment()));
            existing.setProviderStatusCode(form.status_code());
            existing.setProviderStatusDescription(form.status_description());
            existing.setErrorMessage(form.error());
            existing.setRawPayload(form.toString());
            txRepo.save(existing);
            return "PAYMENT_CANCELLED";
        }

        var tx = Transaction.builder()
                .invoiceId(form.invoice_id())
                .orderId(orderId)
                .status("CANCELLED")
                .amount(toBigDecimal(form.amount()))
                .installment(toInteger(form.installment()))
                .providerStatusCode(form.status_code())
                .providerStatusDescription(form.status_description())
                .errorMessage(form.error())
                .rawPayload(form.toString())
                .build();
        txRepo.save(tx);
        return "PAYMENT_CANCELLED";
    }

    private BigDecimal toBigDecimal(String s) {
        try { return (s == null || s.isBlank()) ? null : new BigDecimal(s); }
        catch (Exception e) { return null; }
    }

    private Integer toInteger(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.parseInt(s); }
        catch (Exception e) { return null; }
    }
}