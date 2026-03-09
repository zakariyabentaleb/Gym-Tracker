package com.gymtracker.dto;

import java.util.List;

public record MeResponse(Long id, String username, List<String> roles) {
}

