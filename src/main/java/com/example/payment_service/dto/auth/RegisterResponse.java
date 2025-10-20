package com.example.payment_service.dto.auth;

import java.time.LocalDateTime;

public record RegisterResponse(
        Long id,
        String phone,
        LocalDateTime createdAt
) {}

