package com.gymtracker.mapper;

import com.gymtracker.entity.CourseSchedule;

public class CourseScheduleMapper {

    public static com.gymtracker.dto.CourseScheduleResponse toDto(CourseSchedule s) {
        if (s == null) return null;
        com.gymtracker.dto.CourseScheduleResponse dto = new com.gymtracker.dto.CourseScheduleResponse();
        dto.setId(s.getId());
        dto.setCourseId(s.getCourseId());
        dto.setCoachId(s.getCoachId());
        dto.setRoom(s.getRoom());
        dto.setStartTime(s.getStartTime());
        dto.setEndTime(s.getEndTime());
        dto.setCapacity(s.getCapacity());
        dto.setActive(s.getActive());
        return dto;
    }

    public static CourseSchedule fromCreateRequest(com.gymtracker.dto.CourseScheduleCreateRequest req) {
        if (req == null) return null;
        return CourseSchedule.builder()
                .courseId(req.getCourseId())
                .coachId(req.getCoachId())
                .room(req.getRoom())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .capacity(req.getCapacity())
                .active(req.getActive())
                .build();
    }
}
