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
    private final com.example.payment_service.security.CurrentUser currentUser;
    private final com.example.payment_service.repos.UserRepository userRepo;
    private final com.example.payment_service.repos.TransactionRepository txRepo;

    @Value("${sipay.app-secret}")   private String appSecret;
    @Value("${sipay.merchant-key}") private String merchantKey;

    private static String twoDecimals(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP)
                .toPlainString()
                .replace(',', '.'); // US nokta formatı güvence
    }

    @Override
    public String start3DPayment(DirectPaymentRequest r) {
        // 0) İstek yapan kullanıcı
        Long userId = currentUser.get();                     // JwtAuthFilter set ediyor
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // 1) Token al (mevcut)
        var tokenResp = tokenService.getToken();
        var token = tokenResp.data() != null ? tokenResp.data().token() : null;
        if (token == null || token.isBlank()) throw new PaymentRequestException("Token alınamadı");

        // 2) Tutar/format (mevcut)
        var total = new java.math.BigDecimal(String.valueOf(r.total()));
        var totalStr = twoDecimals(total);
        int installment = r.installmentsNumber();

        // 3) Hash (mevcut)
        String hashKey = generateHashKey(totalStr, installment, r.currencyCode(),
                merchantKey, r.invoiceId(), appSecret);

        // 3.5) PENDING Transaction (yoksa) — orderId henüz bilinmiyor
        if (!txRepo.existsByInvoiceId(r.invoiceId())) {
            var tx = com.example.payment_service.entities.Transaction.builder()
                    .invoiceId(r.invoiceId())
                    .orderId(null)                     // callback’te dolacak
                    .status("PENDING")
                    .amount(total)
                    .installment(installment)
                    .currencyCode(r.currencyCode())
                    .user(user)                        // 🔗 kullanıcıyı bağladık
                    .build();
            txRepo.save(tx);
        }

        // 4) Formu hazırla + isteği at (mevcut)
        var form = com.example.payment_service.util.PaymentFormUtil
                .toForm(r, merchantKey, totalStr, installment, hashKey);

        return sipayClient.post()
                .uri("/api/paySmart3D")
                .contentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
                .accept(org.springframework.http.MediaType.TEXT_HTML)
                .headers(h -> h.setBearerAuth(token))
                .bodyValue(form)
                .retrieve()
                .onStatus(st -> st.is4xxClientError() || st.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .map(b -> new PaymentRequestException("paySmart3D hata: "
                                        + resp.statusCode() + " - " + b)))
                .bodyToMono(String.class)
                .block();
    }
}
