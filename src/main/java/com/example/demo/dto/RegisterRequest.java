package com.example.demo.dto;

import com.example.demo.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request")
public class RegisterRequest {
    
    @NotBlank
    @Schema(description = "Username for the new account", example = "john_doe")
    private String username;
    
    @Email
    @NotBlank
    @Schema(description = "Email address for the new account", example = "john@example.com")
    private String email;
    
    @NotBlank
    @Size(min = 6)
    @Schema(description = "Password for the new account (minimum 6 characters)", example = "password123")
    private String password;
    
    @Schema(description = "User role", example = "USER")
    private Role role;

    public RegisterRequest() {}

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
