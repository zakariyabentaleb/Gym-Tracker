package com.gymtracker.service;

import com.gymtracker.dto.PaymentCreateRequest;
import com.gymtracker.entity.Member;
import com.gymtracker.entity.Payment;
import com.gymtracker.entity.Subscription;
import com.gymtracker.mapper.PaymentMapper;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.PaymentRepository;
import com.gymtracker.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MemberRepository memberRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          SubscriptionRepository subscriptionRepository,
                          MemberRepository memberRepository) {
        this.paymentRepository = paymentRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.memberRepository = memberRepository;
    }

    public com.gymtracker.dto.PaymentResponse create(PaymentCreateRequest req) {
        // validate member
        Optional<Member> mOpt = memberRepository.findById(req.getMemberId());
        if (mOpt.isEmpty()) {
            throw new IllegalArgumentException("Member not found: " + req.getMemberId());
        }

        // validate subscription if provided
        if (req.getSubscriptionId() != null) {
            Optional<Subscription> sOpt = subscriptionRepository.findById(req.getSubscriptionId());
            if (sOpt.isEmpty()) {
                throw new IllegalArgumentException("Subscription not found: " + req.getSubscriptionId());
            }
        }

        Payment p = Payment.builder()
                .subscriptionId(req.getSubscriptionId())
                .memberId(req.getMemberId())
                .amountCents(req.getAmountCents())
                .method(req.getMethod())
                .status("COMPLETED")
                .paymentDate(LocalDateTime.now())
                .reference(req.getReference())
                .build();

        try {
            Payment saved = paymentRepository.save(p);
            return PaymentMapper.toDto(saved);
        } catch (Exception ex) {
            // Convert to IllegalArgumentException so GlobalExceptionHandler returns 400 with the cause
            String root = ex.getMessage();
            if (ex.getCause() != null) root = ex.getCause().getMessage() + " | " + root;
            throw new IllegalArgumentException("Failed to save payment: " + root, ex);
        }
    }

    public com.gymtracker.dto.PaymentResponse findById(Long id) {
        Optional<com.gymtracker.entity.Payment> opt = paymentRepository.findById(id);
        if (opt.isEmpty()) return null;
        return PaymentMapper.toDto(opt.get());
    }

    public List<com.gymtracker.dto.PaymentResponse> findByMemberId(Long memberId) {
        List<com.gymtracker.entity.Payment> list = paymentRepository.findByMemberId(memberId);
        List<com.gymtracker.dto.PaymentResponse> out = new ArrayList<>();
        for (com.gymtracker.entity.Payment p : list) out.add(PaymentMapper.toDto(p));
        return out;
    }

    public List<com.gymtracker.dto.PaymentResponse> findBySubscriptionId(Long subscriptionId) {
        List<com.gymtracker.entity.Payment> list = paymentRepository.findBySubscriptionId(subscriptionId);
        List<com.gymtracker.dto.PaymentResponse> out = new ArrayList<>();
        for (com.gymtracker.entity.Payment p : list) out.add(PaymentMapper.toDto(p));
        return out;
    }
}
