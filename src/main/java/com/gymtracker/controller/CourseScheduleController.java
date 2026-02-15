package com.gymtracker.controller;

import com.gymtracker.dto.CourseScheduleCreateRequest;
import com.gymtracker.dto.CourseScheduleResponse;
import com.gymtracker.service.CourseScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-schedules")
@RequiredArgsConstructor
public class CourseScheduleController {

    private final CourseScheduleService scheduleService;

    @GetMapping("/course/{courseId}")
    public List<CourseScheduleResponse> listByCourse(@PathVariable Long courseId) {
        return scheduleService.findByCourseId(courseId);
    }

    @GetMapping("/{id}")
    public CourseScheduleResponse getById(@PathVariable Long id) {
        return scheduleService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public CourseScheduleResponse create(@Valid @RequestBody CourseScheduleCreateRequest req) {
        return scheduleService.create(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public CourseScheduleResponse update(@PathVariable Long id, @Valid @RequestBody CourseScheduleCreateRequest req) {
        return scheduleService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public void delete(@PathVariable Long id) {
        scheduleService.delete(id);
    }
}

