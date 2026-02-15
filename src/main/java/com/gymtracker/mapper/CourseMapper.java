package com.gymtracker.mapper;

import com.gymtracker.entity.Course;

public class CourseMapper {

    public static com.gymtracker.dto.CourseResponse toDto(Course c) {
        if (c == null) return null;
        com.gymtracker.dto.CourseResponse dto = new com.gymtracker.dto.CourseResponse();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setDescription(c.getDescription());
        dto.setDurationMinutes(c.getDurationMinutes());
        dto.setCapacity(c.getCapacity());
        dto.setActive(c.getActive());
        return dto;
    }

    public static Course fromCreateRequest(com.gymtracker.dto.CourseCreateRequest req) {
        if (req == null) return null;
        return Course.builder()
                .name(req.getName())
                .description(req.getDescription())
                .durationMinutes(req.getDurationMinutes())
                .capacity(req.getCapacity())
                .active(req.getActive())
                .build();
    }
}

