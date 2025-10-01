package com.example.payment_service.controllers;

import com.example.payment_service.dto.callback.PaymentCancelForm;
import com.example.payment_service.dto.callback.PaymentReturnForm;
import com.example.payment_service.services.PaymentCallbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentCallbackController {

    private final PaymentCallbackService callbackService;

    @PostMapping("/return")
    public ResponseEntity<String> handleReturn(@ModelAttribute PaymentReturnForm form) {
        return ResponseEntity.ok(callbackService.handleReturn(form));
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> handleCancel(@ModelAttribute PaymentCancelForm form) {
        return ResponseEntity.ok(callbackService.handleCancel(form));
    }
}


