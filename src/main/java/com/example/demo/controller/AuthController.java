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
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok("Registration endpoint - to be implemented in later review");
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and obtain JWT token")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login endpoint - to be implemented in later review");
    }
}