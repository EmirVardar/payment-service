package com.example.payment_service.dto.auth;

import java.time.LocalDateTime;

public record LoginResponse(
        String token,
        Long userId,
        String phone,
        LocalDateTime createdAt
) {}
