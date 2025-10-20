package com.example.payment_service.security;


import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public void set(Long id) { USER_ID.set(id); }
    public Long get() { return USER_ID.get(); }
    public void clear() { USER_ID.remove(); }
}
