package com.gymtracker.dto;

import lombok.Data;

@Data
public class CourseCreateRequest {
    private String name;
    private String description;
    private Integer durationMinutes;
    private Integer capacity;
    private Boolean active = true;
}

