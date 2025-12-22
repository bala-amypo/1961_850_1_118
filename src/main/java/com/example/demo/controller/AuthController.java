package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;       
import com.example.demo.dto.RegisterRequest;    
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and registration")
public class AuthController {

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
       
        return ResponseEntity.ok("User registered: " + request.getUsername());
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and obtain JWT token")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
    
        return ResponseEntity.ok("Login successful for: " + request.getUsername());
    }
}