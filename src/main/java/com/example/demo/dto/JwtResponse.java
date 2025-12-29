package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication response")
public class JwtResponse {
    
    @JsonProperty("token")
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @JsonProperty("type")
    @Schema(description = "Token type", example = "Bearer")
    private String type = "Bearer";
    
    @JsonProperty("username")
    @Schema(description = "Username", example = "john_doe")
    private String username;
    
    @JsonProperty("email")
    @Schema(description = "User email", example = "john@example.com")
    private String email;

    public JwtResponse() {}

    public JwtResponse(String token, String username, String email) {
        this.token = token;
        this.username = username;
        this.email = email;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
