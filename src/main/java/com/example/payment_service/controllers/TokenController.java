package com.example.payment_service.controllers;

import com.example.payment_service.dto.TokenResponse;
import com.example.payment_service.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @GetMapping("/get")
    public ResponseEntity<TokenResponse> getToken() {
        TokenResponse response = tokenService.getToken();
        return ResponseEntity.ok(response);
    }
}

