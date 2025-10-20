package com.example.payment_service.services.impl;

import com.example.payment_service.dto.auth.LoginRequest;
import com.example.payment_service.dto.auth.RegisterRequest;
import com.example.payment_service.entities.User;
import com.example.payment_service.repos.UserRepository;
import com.example.payment_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final com.example.payment_service.mapper.UserMapper userMapper; // 👈 EKLE

    @Override
    public User register(RegisterRequest req) {
        if (userRepo.existsByPhone(req.phone())) {
            throw new IllegalArgumentException("Bu telefon numarası zaten kayıtlı.");
        }
        // Builder yerine mapper
        User user = userMapper.toEntity(req);
        return userRepo.save(user);
    }

    @Override
    public User authenticate(LoginRequest req) {
        var user = userRepo.findByPhone(req.phone())
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı"));
        if (!user.getPassword().equals(req.password())) {
            throw new IllegalArgumentException("Geçersiz şifre");
        }
        return user;
    }
}
