package com.gymtracker.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.gymtracker.dto.PaymentCreateRequest;
import com.gymtracker.dto.SubscriptionCreateRequest;
import com.gymtracker.dto.SubscriptionResponse;
import com.gymtracker.entity.AppUser;
import com.gymtracker.entity.Member;
import com.gymtracker.entity.SubscriptionPlan;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.SubscriptionPlanRepository;
import com.gymtracker.repository.UserRepository;
import com.gymtracker.service.PayPalService;
import com.gymtracker.service.PaymentService;
import com.gymtracker.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
public class PayPalController {

    private final PayPalService payPalService;
    private final SubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final SubscriptionPlanRepository planRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    /**
     * Create a PayPal order for a subscription plan.
     * Body: { "planId": 1 }
     * Returns: { "orderId": "PAYPAL_ORDER_ID" }
     */
    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public Map<String, String> createOrder(@RequestBody Map<String, Long> body) {
        Long planId = body.get("planId");
        if (planId == null) {
            throw new IllegalArgumentException("planId is required");
        }

        Optional<SubscriptionPlan> planOpt = planRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Plan not found: " + planId);
        }

        SubscriptionPlan plan = planOpt.get();
        String orderId = payPalService.createOrder(plan.getPriceCents(), plan.getName());

        return Map.of("orderId", orderId);
    }

    /**
     * Capture a PayPal order after user approval, then create Subscription + Payment.
     * Body: { "orderId": "PAYPAL_ORDER_ID", "planId": 1 }
     * Returns: SubscriptionResponse
     */
    @PostMapping("/capture-order")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public SubscriptionResponse captureOrder(
            Authentication authentication,
            @RequestBody Map<String, Object> body
    ) {
        String orderId = (String) body.get("orderId");
        Number planIdNum = (Number) body.get("planId");

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId is required");
        }
        if (!orderId.matches("^[A-Za-z0-9]+$")) {
            throw new IllegalArgumentException("Invalid orderId format");
        }
        if (planIdNum == null) {
            throw new IllegalArgumentException("planId is required");
        }
        long planId = planIdNum.longValue();

        // Capture the PayPal payment
        JsonNode captureResult = payPalService.captureOrder(orderId);

        // Resolve member from JWT
        String username = authentication.getName();
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        Member member = memberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found for user: " + user.getId()));

        // Retrieve plan for price
        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));

        // Create subscription
        SubscriptionCreateRequest subReq = new SubscriptionCreateRequest();
        subReq.setMemberId(member.getId());
        subReq.setPlanId(planId);
        subReq.setAutoRenew(false);
        SubscriptionResponse subscription = subscriptionService.create(subReq);

        // Record payment
        PaymentCreateRequest payReq = new PaymentCreateRequest();
        payReq.setMemberId(member.getId());
        payReq.setSubscriptionId(subscription.getId());
        payReq.setAmountCents(plan.getPriceCents());
        payReq.setMethod("PAYPAL");
        payReq.setReference(orderId);
        paymentService.create(payReq);

        return subscription;
    }
}
