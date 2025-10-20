package com.example.payment_service.mapper;

import com.example.payment_service.dto.DirectPaymentRequest;
import com.example.payment_service.dto.callback.PaymentCancelForm;
import com.example.payment_service.dto.callback.PaymentReturnForm;
import com.example.payment_service.entities.Transaction;
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    // ---------- PENDING: DirectPaymentRequest -> Transaction ----------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoiceId", source = "req.invoiceId")
    @Mapping(target = "orderId", ignore = true)                 // 3D callback'te dolacak
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "amount", source = "total")
    @Mapping(target = "installment", source = "installment")
    @Mapping(target = "currencyCode", source = "req.currencyCode")
    @Mapping(target = "authCode", ignore = true)
    @Mapping(target = "providerStatusCode", ignore = true)
    @Mapping(target = "providerStatusDescription", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "rawPayload", ignore = true)
    @Mapping(target = "user", ignore = true)                    // service'te set edilecek
    @Mapping(target = "createdAt", ignore = true)               // @CreationTimestamp
    Transaction toPendingFromDirect(DirectPaymentRequest req,
                                    BigDecimal total,
                                    Integer installment);

    // ---------- RETURN (3D dönüş) ----------
    // Mevcut Transaction'ı güncelle
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "invoiceId", ignore = true)
    @Mapping(target = "orderId", source = "order_id")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "toBigDecimal")
    @Mapping(target = "installment", source = "installment", qualifiedByName = "toInteger")
    @Mapping(target = "authCode", source = "auth_code")
    @Mapping(target = "providerStatusCode", source = "status_code")
    @Mapping(target = "providerStatusDescription", source = "status_description")
    @Mapping(target = "errorMessage", source = "error")
    @Mapping(target = "rawPayload", expression = "java(java.util.Objects.toString(form))")
    void updateFromReturn(PaymentReturnForm form, @MappingTarget Transaction tx);

    // Kayıt yoksa yeni Transaction oluştur
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "invoiceId", source = "invoice_id")
    @Mapping(target = "orderId", source = "order_id")
    @Mapping(target = "status", ignore = true) // @AfterMapping'te setlenecek
    @Mapping(target = "amount", source = "amount", qualifiedByName = "toBigDecimal")
    @Mapping(target = "installment", source = "installment", qualifiedByName = "toInteger")
    @Mapping(target = "authCode", source = "auth_code")
    @Mapping(target = "providerStatusCode", source = "status_code")
    @Mapping(target = "providerStatusDescription", source = "status_description")
    @Mapping(target = "errorMessage", source = "error")
    @Mapping(target = "rawPayload", expression = "java(java.util.Objects.toString(form))")
    @Mapping(target = "currencyCode", ignore = true) // formda yok, istersen IGNORE
    @Mapping(target = "createdAt", ignore = true)
    Transaction newFromReturn(PaymentReturnForm form);

    // ---------- CANCEL (iptal) ----------
    // Mevcut Transaction'ı güncelle
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "invoiceId", ignore = true)
    @Mapping(target = "orderId", source = "order_id")
    @Mapping(target = "status", constant = "CANCELLED")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "toBigDecimal")
    @Mapping(target = "installment", source = "installment", qualifiedByName = "toInteger")
    @Mapping(target = "providerStatusCode", source = "status_code")
    @Mapping(target = "providerStatusDescription", source = "status_description")
    @Mapping(target = "errorMessage", source = "error")
    @Mapping(target = "rawPayload", expression = "java(java.util.Objects.toString(form))")
    @Mapping(target = "authCode", ignore = true) // iptalde gelmiyor
    void updateFromCancel(PaymentCancelForm form, @MappingTarget Transaction tx);

    // Kayıt yoksa yeni Transaction oluştur
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "invoiceId", source = "invoice_id")
    @Mapping(target = "orderId", source = "order_id")
    @Mapping(target = "status", constant = "CANCELLED")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "toBigDecimal")
    @Mapping(target = "installment", source = "installment", qualifiedByName = "toInteger")
    @Mapping(target = "providerStatusCode", source = "status_code")
    @Mapping(target = "providerStatusDescription", source = "status_description")
    @Mapping(target = "errorMessage", source = "error")
    @Mapping(target = "rawPayload", expression = "java(java.util.Objects.toString(form))")
    @Mapping(target = "currencyCode", ignore = true) // formda yok, istersen IGNORE
    @Mapping(target = "createdAt", ignore = true)
    Transaction newFromCancel(PaymentCancelForm form);

    // ---------- Converters ----------
    @Named("toBigDecimal")
    default BigDecimal toBigDecimal(String s) {
        try { return (s == null || s.isBlank()) ? null : new BigDecimal(s); }
        catch (Exception e) { return null; }
    }

    @Named("toInteger")
    default Integer toInteger(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.parseInt(s); }
        catch (Exception e) { return null; }
    }

    // RETURN: md_status -> status
    @AfterMapping
    default void setStatusFromReturn(PaymentReturnForm form, @MappingTarget Transaction tx) {
        tx.setStatus("1".equals(form.md_status()) ? "SUCCESS" : "FAILED");
    }
}
