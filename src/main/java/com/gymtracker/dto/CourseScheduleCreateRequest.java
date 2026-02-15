package com.gymtracker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseScheduleCreateRequest {
    private Long courseId;
    private Long coachId;
    private String room;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private Boolean active = true;
}

