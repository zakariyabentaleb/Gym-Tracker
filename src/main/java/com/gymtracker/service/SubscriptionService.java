package com.gymtracker.service;

import com.gymtracker.dto.SubscriptionCreateRequest;
import com.gymtracker.dto.SubscriptionUpdateRequest;
import com.gymtracker.entity.Subscription;
import com.gymtracker.entity.SubscriptionPlan;
import com.gymtracker.entity.Member;
import com.gymtracker.mapper.SubscriptionMapper;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.SubscriptionPlanRepository;
import com.gymtracker.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MemberRepository memberRepository;
    private final SubscriptionPlanRepository planRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               MemberRepository memberRepository,
                               SubscriptionPlanRepository planRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.memberRepository = memberRepository;
        this.planRepository = planRepository;
    }

    public com.gymtracker.dto.SubscriptionResponse create(SubscriptionCreateRequest req) {
        // Validate member
        Optional<Member> memberOpt = memberRepository.findById(req.getMemberId());
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Member not found: " + req.getMemberId());
        }

        // Validate plan
        Optional<SubscriptionPlan> planOpt = planRepository.findById(req.getPlanId());
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Subscription plan not found: " + req.getPlanId());
        }

        SubscriptionPlan plan = planOpt.get();
        LocalDate start = req.getStartDate() != null ? req.getStartDate() : LocalDate.now();
        LocalDate end = start.plusDays(plan.getDurationDays() - 1L);

        // Check overlapping ACTIVE subscription for same member
        List<Subscription> existing = subscriptionRepository.findByMemberId(req.getMemberId());
        boolean overlap = existing.stream().anyMatch(s -> "ACTIVE".equalsIgnoreCase(s.getStatus()) && datesOverlap(s.getStartDate(), s.getEndDate(), start, end));
        if (overlap) {
            throw new IllegalStateException("Member already has an active overlapping subscription");
        }

        Subscription subs = Subscription.builder()
                .memberId(req.getMemberId())
                .planId(req.getPlanId())
                .startDate(start)
                .endDate(end)
                .status("ACTIVE")
                .autoRenew(req.getAutoRenew() != null && req.getAutoRenew())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Subscription saved = subscriptionRepository.save(subs);
        return SubscriptionMapper.toDto(saved);
    }

    public com.gymtracker.dto.SubscriptionResponse findById(Long id) {
        Optional<Subscription> opt = subscriptionRepository.findById(id);
        if (opt.isEmpty()) return null;
        return SubscriptionMapper.toDto(opt.get());
    }

    public List<com.gymtracker.dto.SubscriptionResponse> findByMemberId(Long memberId) {
        List<Subscription> subs = subscriptionRepository.findByMemberId(memberId);
        List<com.gymtracker.dto.SubscriptionResponse> out = new ArrayList<>();
        for (Subscription s : subs) {
            out.add(SubscriptionMapper.toDto(s));
        }
        return out;
    }

    public com.gymtracker.dto.SubscriptionResponse update(Long id, SubscriptionUpdateRequest req) {
        Optional<Subscription> opt = subscriptionRepository.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("Subscription not found: " + id);
        Subscription s = opt.get();
        if (req.getStatus() != null) {
            // validate status value roughly
            s.setStatus(req.getStatus());
        }
        if (req.getAutoRenew() != null) {
            s.setAutoRenew(req.getAutoRenew());
        }
        s.setUpdatedAt(LocalDateTime.now());
        Subscription saved = subscriptionRepository.save(s);
        return SubscriptionMapper.toDto(saved);
    }

    public com.gymtracker.dto.SubscriptionResponse renew(Long id) {
        Optional<Subscription> opt = subscriptionRepository.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("Subscription not found: " + id);
        Subscription s = opt.get();
        Optional<SubscriptionPlan> planOpt = planRepository.findById(s.getPlanId());
        if (planOpt.isEmpty()) throw new IllegalArgumentException("Subscription plan not found for plan id: " + s.getPlanId());
        SubscriptionPlan plan = planOpt.get();

        LocalDate candidateStart = s.getEndDate().plusDays(1);
        LocalDate start = candidateStart.isAfter(LocalDate.now()) ? candidateStart : LocalDate.now();
        LocalDate end = start.plusDays(plan.getDurationDays() - 1L);

        s.setStartDate(start);
        s.setEndDate(end);
        s.setStatus("ACTIVE");
        s.setUpdatedAt(LocalDateTime.now());
        Subscription saved = subscriptionRepository.save(s);
        return SubscriptionMapper.toDto(saved);
    }

    private boolean datesOverlap(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
        return (aStart.isBefore(bEnd) || aStart.isEqual(bEnd)) && (bStart.isBefore(aEnd) || bStart.isEqual(aEnd));
    }
}
