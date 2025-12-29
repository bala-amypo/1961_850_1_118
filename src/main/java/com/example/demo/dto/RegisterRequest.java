package com.example.demo.dto;

import com.example.demo.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User registration request")
public class RegisterRequest {
    
    @JsonProperty("username")
    @Schema(description = "Username for the new account", example = "john_doe", required = true)
    private String username;
    
    @JsonProperty("email")
    @Schema(description = "Email address for the new account", example = "john@example.com", required = true)
    private String email;
    
    @JsonProperty("password")
    @Schema(description = "Password for the new account (minimum 6 characters)", example = "password123", required = true)
    private String password;
    
    @JsonProperty("role")
    @Schema(description = "User role", example = "ANALYST")
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
