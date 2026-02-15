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
public class BookingResponse {
    private Long id;
    private Long scheduleId;
    private Long memberId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

