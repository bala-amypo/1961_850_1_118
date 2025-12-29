package com.example.demo.controller;

import com.example.demo.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and registration")
public class AuthController {

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        // Basic validation check
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required");
        }
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters");
        }
        
        return ResponseEntity.ok("Registration successful for user: " + registerRequest.getUsername());
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and obtain JWT token")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login endpoint - to be implemented in later review");
    }
}