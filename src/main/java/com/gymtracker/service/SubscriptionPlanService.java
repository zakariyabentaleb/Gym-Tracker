package com.gymtracker.service;

import com.gymtracker.dto.PlanCreateRequest;
import com.gymtracker.dto.PlanResponse;
import com.gymtracker.dto.PlanUpdateRequest;
import com.gymtracker.entity.SubscriptionPlan;
import com.gymtracker.repository.SubscriptionPlanRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository repository;

    @Transactional
    public PlanResponse create(PlanCreateRequest request) {
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(request.getName())
                .durationDays(request.getDurationDays())
                .priceCents(request.getPriceCents())
                .includesClasses(request.getIncludesClasses() != null ? request.getIncludesClasses() : Boolean.FALSE)
                .description(request.getDescription())
                .active(Boolean.TRUE)
                .build();
        SubscriptionPlan saved = repository.save(plan);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PlanResponse getById(long id) {
        SubscriptionPlan plan = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + id));
        return toResponse(plan);
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> listAll() {
        return ((List<SubscriptionPlan>) repository.findAll()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlanResponse update(long id, PlanUpdateRequest request) {
        SubscriptionPlan plan = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + id));

        if (request.getName() != null) plan.setName(request.getName());
        if (request.getDurationDays() != null) plan.setDurationDays(request.getDurationDays());
        if (request.getPriceCents() != null) plan.setPriceCents(request.getPriceCents());
        if (request.getIncludesClasses() != null) plan.setIncludesClasses(request.getIncludesClasses());
        if (request.getDescription() != null) plan.setDescription(request.getDescription());
        if (request.getActive() != null) plan.setActive(request.getActive());

        return toResponse(repository.save(plan));
    }

    @Transactional
    public void delete(long id) {
        if (!repository.existsById(id)) throw new IllegalArgumentException("Plan not found: " + id);
        repository.deleteById(id);
    }

    private PlanResponse toResponse(SubscriptionPlan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDurationDays(),
                plan.getPriceCents(),
                plan.getIncludesClasses(),
                plan.getDescription(),
                plan.getActive()
        );
    }
}
