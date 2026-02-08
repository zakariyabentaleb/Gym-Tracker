package com.gymtracker.dto;

import java.util.List;

public record MeResponse(String username, List<String> roles) {
}

