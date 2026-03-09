package com.gymtracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CoachCreateRequest {
    private Long userId;

    @NotBlank
    private String displayName;

    private String phone;
    private String bio;
    private String certifications;
    private String photoUrl;
    private Boolean active;
}

