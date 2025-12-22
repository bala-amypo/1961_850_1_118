package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;       
import com.example.demo.dto.RegisterRequest;    
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@PostMapping("/register")
@Operation(summary = "Register new user")
public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
    // 1. Check if email exists (instead of username)
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        return ResponseEntity.badRequest().body("Email already exists!");
    }

    // 2. Create User with new fields
    AppUser newUser = new AppUser();
    newUser.setEmail(request.getEmail());
    newUser.setPassword(request.getPassword());
    newUser.setRole(request.getRole()); // Crucial: Save the role
    
    // 3. Save
    userRepository.save(newUser);

    return ResponseEntity.ok("User registered successfully!");
}