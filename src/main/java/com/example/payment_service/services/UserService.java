package com.example.payment_service.services;

import com.example.payment_service.dto.auth.LoginRequest;
import com.example.payment_service.dto.auth.RegisterRequest;
import com.example.payment_service.entities.User;

public interface UserService {
    User register(RegisterRequest req);
    User authenticate(LoginRequest req);

}
