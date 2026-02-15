package com.gymtracker.dto;

import lombok.Data;

@Data
public class BookingCreateRequest {
    private Long scheduleId;
    private Long memberId; // optional when called from /me
}

