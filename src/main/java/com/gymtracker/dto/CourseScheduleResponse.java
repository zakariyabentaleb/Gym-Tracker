package com.gymtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseScheduleResponse {
    private Long id;
    private Long courseId;
    private Long coachId;
    private String room;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private Boolean active;
}

