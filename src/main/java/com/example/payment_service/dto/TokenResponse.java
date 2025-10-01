package com.example.payment_service.dto;

public record TokenResponse(
        int status_code,
        String status_description,
        TokenData data
) {
    public record TokenData(String token) {}
}

