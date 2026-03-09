package com.gymtracker.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private final String token;
    private final String tokenType;
    private final Long userId;

    public AuthResponse(String token, Long userId) {
        this.token = token;
        this.tokenType = "Bearer";
        this.userId = userId;
    }
}
