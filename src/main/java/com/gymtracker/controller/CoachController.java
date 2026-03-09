package com.gymtracker.controller;

import com.gymtracker.dto.CoachCreateRequest;
import com.gymtracker.dto.CoachResponse;
import com.gymtracker.service.CoachService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coaches")
@RequiredArgsConstructor
public class CoachController {

    private final CoachService coachService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public CoachResponse create(@Valid @RequestBody CoachCreateRequest req) {
        return coachService.create(req);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public List<CoachResponse> listAll() {
        return coachService.listAll();
    }

    @GetMapping("/active")
    public List<CoachResponse> listActive() {
        return coachService.listActive();
    }

    @GetMapping("/{id}")
    public CoachResponse get(@PathVariable Long id) {
        return coachService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public CoachResponse update(@PathVariable Long id, @Valid @RequestBody CoachCreateRequest req) {
        return coachService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public void delete(@PathVariable Long id) {
        coachService.delete(id);
    }

    @PatchMapping("/{id}/assign-schedule/{scheduleId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public void assignToSchedule(@PathVariable Long id, @PathVariable Long scheduleId) {
        coachService.assignToSchedule(scheduleId, id);
    }

    /**
     * Return schedules for the authenticated coach user (ROLE_COACH). Uses the authenticated username to
     * resolve the user->coach mapping.
     */
    @GetMapping("/me/schedules")
    @PreAuthorize("hasAuthority('ROLE_COACH')")
    public List<com.gymtracker.dto.CourseScheduleResponse> mySchedules(Authentication authentication) {
        String username = authentication.getName();
        return coachService.getSchedulesForUser(username);
    }

    @GetMapping("/{id}/schedules")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public List<com.gymtracker.dto.CourseScheduleResponse> getSchedulesForCoach(@PathVariable Long id) {
        return coachService.getSchedulesForCoachId(id);
    }
}
