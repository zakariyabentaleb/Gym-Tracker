package com.gymtracker.mapper;

import com.gymtracker.entity.Booking;

public class BookingMapper {

    public static com.gymtracker.dto.BookingResponse toDto(Booking b) {
        if (b == null) return null;
        com.gymtracker.dto.BookingResponse dto = new com.gymtracker.dto.BookingResponse();
        dto.setId(b.getId());
        dto.setScheduleId(b.getScheduleId());
        dto.setMemberId(b.getMemberId());
        dto.setStatus(b.getStatus());
        dto.setCreatedAt(b.getCreatedAt());
        dto.setUpdatedAt(b.getUpdatedAt());
        return dto;
    }
}

