package com.example.payment_service.controllers;

import com.example.payment_service.dto.DirectPaymentRequest;
import com.example.payment_service.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid; // ✅ doğru paket

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(
            value = "/directPayment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> directPayment(@Valid @RequestBody DirectPaymentRequest req) {
        String html = paymentService.start3DPayment(req);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html); // bankaya yönlendiren HTML
    }
}
