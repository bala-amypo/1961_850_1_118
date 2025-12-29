package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User login request")
public class LoginRequest {
    
    @JsonProperty("username")
    @Schema(description = "Username or email", example = "john_doe", required = true)
    private String username;
    
    @JsonProperty("password")
    @Schema(description = "User password", example = "password123", required = true)
    private String password;

    public LoginRequest() {}

    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }

    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
}