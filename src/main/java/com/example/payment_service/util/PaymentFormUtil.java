package com.example.payment_service.util;

import com.example.payment_service.dto.DirectPaymentRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class PaymentFormUtil {

    private static final ObjectMapper om = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    private PaymentFormUtil() {
        // Utility class -> constructor private (kimse new'leyemesin)
    }

    public static MultiValueMap<String, String> toForm(DirectPaymentRequest r,
                                                       String merchantKey,
                                                       String totalStr,
                                                       int installment,
                                                       String hashKey) {
        try {
            // DTO -> Map (snake_case key)
            Map<String, Object> base = om.convertValue(r, new TypeReference<>() {});

            // özel override alanları
            base.put("merchant_key", merchantKey);
            base.put("total", totalStr);
            base.put("installments_number", installment);
            base.put("response_method", "POST");
            base.put("hash_key", hashKey);

            // Map -> MultiValueMap
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            base.forEach((k, v) -> {
                if (v != null) form.add(k, String.valueOf(v));
            });

            return form;
        } catch (Exception ex) {
            throw new IllegalStateException("Form oluşturulamadı", ex);
        }
    }
}
