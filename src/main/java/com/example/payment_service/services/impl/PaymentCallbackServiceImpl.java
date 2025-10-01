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

        // idempotency: aynı order_id geldiyse tekrar kaydetme
        if (orderId != null && txRepo.existsByOrderId(orderId)) {
            log.info("Return callback ignored (already processed). orderId={}", orderId);
            return ok ? "PAYMENT_OK" : "PAYMENT_NOT_OK";
        }

        Transaction tx = Transaction.builder()
                .invoiceId(form.invoice_id())
                .orderId(orderId)
                .status(ok ? "SUCCESS" : "FAILED")
                .amount(toBigDecimal(form.amount()))
                .installment(toInteger(form.installment()))
                .authCode(form.auth_code())
                .providerStatusCode(form.status_code())
                .providerStatusDescription(form.status_description())
                .errorMessage(form.error())
                .rawPayload(form.toString()) // istersen kaldır
                .build();

        txRepo.save(tx);
        log.info("Transaction saved (return). orderId={} status={}", orderId, tx.getStatus());
        return ok ? "PAYMENT_OK" : "PAYMENT_NOT_OK";
    }

    @Override
    public String handleCancel(PaymentCancelForm form) {
        String orderId = form.order_id();

        if (orderId != null && txRepo.existsByOrderId(orderId)) {
            log.info("Cancel callback ignored (already processed). orderId={}", orderId);
            return "PAYMENT_CANCELLED";
        }

        Transaction tx = Transaction.builder()
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
        log.info("Transaction saved (cancel). orderId={} status=CANCELLED", orderId);
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