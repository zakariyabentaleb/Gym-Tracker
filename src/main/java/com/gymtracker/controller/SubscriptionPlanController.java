package com.gymtracker.controller;

import com.gymtracker.dto.PlanCreateRequest;
import com.gymtracker.dto.PlanResponse;
import com.gymtracker.dto.PlanUpdateRequest;
import com.gymtracker.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService planService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PlanResponse create(@Valid @RequestBody PlanCreateRequest request) {
        return planService.create(request);
    }

    @GetMapping
    public List<PlanResponse> list() {
        return planService.listAll();
    }

    @GetMapping("/{id}")
    public PlanResponse get(@PathVariable long id) {
        return planService.getById(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PlanResponse update(@PathVariable long id, @Valid @RequestBody PlanUpdateRequest request) {
        return planService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable long id) {
        planService.delete(id);
    }
}

