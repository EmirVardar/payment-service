package com.example.payment_service.services.impl;

import com.example.payment_service.dto.DirectPaymentRequest;
import com.example.payment_service.dto.TokenResponse;
import com.example.payment_service.services.PaymentService;
import com.example.payment_service.services.TokenService;
import com.example.payment_service.exception.PaymentRequestException; // <-- ekledik
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.example.payment_service.util.SipayHashUtil.generateHashKey;
import static com.example.payment_service.util.PaymentFormUtil.toForm; // <-- util metodumuz

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final WebClient sipayClient;
    private final TokenService tokenService;

    @Value("${sipay.app-secret}")   private String appSecret;
    @Value("${sipay.merchant-key}") private String merchantKey;

    private static String twoDecimals(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP)
                .toPlainString()
                .replace(',', '.'); // US nokta formatı güvence
    }

    @Override
    public String start3DPayment(DirectPaymentRequest r) {
        // 1) Token al
        TokenResponse tokenResp = tokenService.getToken();
        String token = tokenResp.data() != null ? tokenResp.data().token() : null;
        if (token == null || token.isBlank()) {
            throw new PaymentRequestException("Token alınamadı ya da boş döndü");
        }

        // 2) Tutar/hesaplamalar
        BigDecimal total = BigDecimal.valueOf(r.total());
        String totalStr = twoDecimals(total);
        int installment = r.installmentsNumber();

        // 3) Hash üret
        String hashKey = generateHashKey(
                totalStr,
                installment,
                r.currencyCode(),
                merchantKey,
                r.invoiceId(),
                appSecret
        );

        // 4) Formu tek satırda hazırla (DTO -> form-urlencoded)
        MultiValueMap<String, String> form =
                toForm(r, merchantKey, totalStr, installment, hashKey);

        // 5) İstek at + hata yakalama
        return sipayClient.post()
                .uri("/api/paySmart3D")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.TEXT_HTML)
                .headers(h -> h.setBearerAuth(token))
                .bodyValue(form)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .map(body -> new PaymentRequestException(
                                        "paySmart3D çağrısı başarısız: " + resp.statusCode() + " - " + body))
                )
                .bodyToMono(String.class)
                .block();
    }
}
