package com.example.payment_service.services.impl;

import com.example.payment_service.dto.TokenRequest;
import com.example.payment_service.dto.TokenResponse;
import com.example.payment_service.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final WebClient sipayClient;

    @Value("${sipay.app-id}") private String appId;

    @Value("${sipay.app-secret}") private String appSecret;

    @Override
    public TokenResponse getToken() {
        TokenRequest request = new TokenRequest(appId, appSecret);

        return sipayClient.post()
                .uri("/api/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }
}
