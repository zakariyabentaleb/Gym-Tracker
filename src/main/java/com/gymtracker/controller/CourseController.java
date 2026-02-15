package com.gymtracker.controller;

import com.gymtracker.dto.CourseCreateRequest;
import com.gymtracker.dto.CourseResponse;
import com.gymtracker.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public List<CourseResponse> list() {
        return courseService.findAll();
    }

    @GetMapping("/{id}")
    public CourseResponse getById(@PathVariable Long id) {
        return courseService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public CourseResponse create(@Valid @RequestBody CourseCreateRequest req) {
        return courseService.create(req);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public CourseResponse update(@PathVariable Long id, @Valid @RequestBody CourseCreateRequest req) {
        return courseService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }
}

