package com.example.payment_service.controllers;

import com.example.payment_service.dto.auth.LoginRequest;
import com.example.payment_service.dto.auth.LoginResponse;
import com.example.payment_service.dto.auth.RegisterRequest;
import com.example.payment_service.dto.auth.RegisterResponse;
import com.example.payment_service.entities.User;
import com.example.payment_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final com.example.payment_service.security.JwtService jwtService; // <-- EKLE

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest req) {
        User user = userService.register(req);
        RegisterResponse dto = new RegisterResponse(
                user.getId(), user.getPhone(), user.getCreatedAt()
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        User u = userService.authenticate(req);
        String token = jwtService.generateToken(u.getId(), u.getPhone());
        return ResponseEntity.ok(new LoginResponse(token, u.getId(), u.getPhone(), u.getCreatedAt()));
    }

}
