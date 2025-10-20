package com.example.payment_service.mapper;

import com.example.payment_service.dto.auth.LoginResponse;
import com.example.payment_service.dto.auth.RegisterRequest;
import com.example.payment_service.dto.auth.RegisterResponse;
import com.example.payment_service.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // RegisterRequest -> User (entity oluşturma)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "createdAt", ignore = true) // @CreationTimestamp DB doldurur
    User toEntity(RegisterRequest req);

    // User -> RegisterResponse (istersen controller’da kullanırsın)
    RegisterResponse toRegisterResponse(User user);

    // User + token(String) -> LoginResponse (token daha sonra lazım olacak)
    @Mapping(target = "token", source = "token")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "phone",  source = "user.phone")
    @Mapping(target = "createdAt", source = "user.createdAt")
    LoginResponse toLoginResponse(User user, String token);
}