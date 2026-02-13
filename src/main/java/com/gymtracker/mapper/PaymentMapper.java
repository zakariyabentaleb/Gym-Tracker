package com.gymtracker.mapper;

import com.gymtracker.entity.Payment;

public class PaymentMapper {

    public static com.gymtracker.dto.PaymentResponse toDto(Payment p) {
        if (p == null) return null;
        com.gymtracker.dto.PaymentResponse resp = new com.gymtracker.dto.PaymentResponse();
        resp.setId(p.getId());
        resp.setSubscriptionId(p.getSubscriptionId());
        resp.setMemberId(p.getMemberId());
        resp.setAmountCents(p.getAmountCents());
        resp.setMethod(p.getMethod());
        resp.setStatus(p.getStatus());
        resp.setPaymentDate(p.getPaymentDate());
        resp.setReference(p.getReference());
        return resp;
    }
}

