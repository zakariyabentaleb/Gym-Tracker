package com.gymtracker.controller;

import com.gymtracker.dto.SubscriptionCreateRequest;
import com.gymtracker.dto.SubscriptionResponse;
import com.gymtracker.dto.SubscriptionUpdateRequest;
import com.gymtracker.entity.AppUser;
import com.gymtracker.entity.Member;
import com.gymtracker.repository.UserRepository;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public SubscriptionResponse create(@Valid @RequestBody SubscriptionCreateRequest request) {
        return subscriptionService.create(request);
    }

    /**
     * Allow a logged-in member to create a subscription for themselves.
     */
    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public SubscriptionResponse createForMe(Authentication authentication, @Valid @RequestBody SubscriptionCreateRequest request) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        String username = authentication.getName();

        Optional<AppUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        Long userId = userOpt.get().getId();
        if (userId == null) {
            throw new IllegalArgumentException("User id missing for username: " + username);
        }

        Optional<Member> memberOpt = memberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Member not found for user: " + userId);
        }
        Long memberId = memberOpt.get().getId();
        request.setMemberId(memberId);
        return subscriptionService.create(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public SubscriptionResponse getById(@PathVariable Long id) {
        return subscriptionService.findById(id);
    }

    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public List<SubscriptionResponse> getByMember(@PathVariable Long memberId) {
        return subscriptionService.findByMemberId(memberId);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public List<SubscriptionResponse> mySubscriptions(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        String username = authentication.getName();

        Optional<AppUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        Long userId = userOpt.get().getId();
        if (userId == null) {
            throw new IllegalArgumentException("User id missing for username: " + username);
        }

        Optional<Member> memberOpt = memberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Member not found for user: " + userId);
        }
        Long memberId = memberOpt.get().getId();
        return subscriptionService.findByMemberId(memberId);
    }

    // New endpoints: update and renew

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public SubscriptionResponse update(@PathVariable Long id, @Valid @RequestBody SubscriptionUpdateRequest request) {
        return subscriptionService.update(id, request);
    }

    @PostMapping("/{id}/renew")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public SubscriptionResponse renew(@PathVariable Long id) {
        return subscriptionService.renew(id);
    }
}
