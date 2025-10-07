package com.example.payment_service.dto.auth;

public record RegisterRequest(
        String phone,
        String password
) {}
