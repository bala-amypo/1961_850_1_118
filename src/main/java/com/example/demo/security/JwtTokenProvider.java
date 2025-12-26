package com.example.demo.security;

import com.example.demo.model.AppUser;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    public String generateToken(AppUser user) {
        return "TOKEN123";
    }
    
    public boolean validateToken(String token) {
        return "VALID".equals(token);
    }
}