package com.example.payment_service.controllers;

import com.example.payment_service.dto.auth.LoginRequest;
import com.example.payment_service.dto.auth.LoginResponse;
import com.example.payment_service.dto.auth.RegisterRequest;
import com.example.payment_service.dto.auth.RegisterResponse;
import com.example.payment_service.entities.User;
import com.example.payment_service.mapper.UserMapper; // 👈 EKLENDİ
import com.example.payment_service.services.UserService;
import com.example.payment_service.security.JwtService; // importunu netleştir
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;       // zaten vardı
    private final UserMapper userMapper;       // 👈 EKLENDİ

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest req) {
        User user = userService.register(req);
        // Elle DTO kurmak yerine mapper:
        RegisterResponse dto = userMapper.toRegisterResponse(user);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        User u = userService.authenticate(req);
        String token = jwtService.generateToken(u.getId(), u.getPhone());
        // Elle DTO kurmak yerine mapper:
        LoginResponse dto = userMapper.toLoginResponse(u, token);
        return ResponseEntity.ok(dto);
    }
}
