package com.gymtracker.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private final String token;
    private final String tokenType;

    public AuthResponse(String token) {
        this.token = token;
        this.tokenType = "Bearer";
    }
}
