package com.gymtracker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CoachResponse {
    private Long id;
    private Long userId;
    private String displayName;
    private String phone;
    private String bio;
    private String certifications;
    private String photoUrl;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

