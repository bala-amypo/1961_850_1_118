package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.AppUser;
import com.example.demo.repository.AppUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication APIs")
public class AuthController {

    private final AppUserRepository appUserRepository;

    public AuthController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        if (appUserRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Email already exists!");
        }

        AppUser newUser = new AppUser();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setRole(request.getRole());

        appUserRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        AppUser user = appUserRepository.findByEmail(request.getEmail()).orElse(null);

        if (user != null && user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.ok("Login Successful! Role: " + user.getRole());
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}