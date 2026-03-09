package com.gymtracker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WaitlistResponse {
    private Long id;
    private Long scheduleId;
    private Long memberId;
    private Integer position;
    private LocalDateTime createdAt;
}

