package com.example.payment_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CurrentUser currentUser;

    public JwtAuthFilter(JwtService jwtService, CurrentUser currentUser) {
        this.jwtService = jwtService;
        this.currentUser = currentUser;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        try {
            String h = req.getHeader("Authorization");
            if (h != null && h.startsWith("Bearer ")) {
                String token = h.substring(7);
                Long userId = jwtService.parseUserId(token);
                currentUser.set(userId);
            }
            chain.doFilter(req, res);
        } finally {
            currentUser.clear(); // thread leak olmasın
        }
    }
}
