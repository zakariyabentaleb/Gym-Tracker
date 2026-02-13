package com.gymtracker.controller;

import com.gymtracker.dto.PaymentCreateRequest;
import com.gymtracker.dto.PaymentResponse;
import com.gymtracker.entity.AppUser;
import com.gymtracker.entity.Member;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.UserRepository;
import com.gymtracker.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public PaymentResponse create(@Valid @RequestBody PaymentCreateRequest request) {
        return paymentService.create(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public PaymentResponse getById(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public List<PaymentResponse> getByMember(@PathVariable Long memberId) {
        return paymentService.findByMemberId(memberId);
    }

    @GetMapping("/subscription/{subscriptionId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public List<PaymentResponse> getBySubscription(@PathVariable Long subscriptionId) {
        return paymentService.findBySubscriptionId(subscriptionId);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public List<PaymentResponse> myPayments(Authentication authentication) {
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

        return paymentService.findByMemberId(memberId);
    }
}

