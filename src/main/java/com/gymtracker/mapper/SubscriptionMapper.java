package com.gymtracker.mapper;

import com.gymtracker.entity.Subscription;

public class SubscriptionMapper {

    public static com.gymtracker.dto.SubscriptionResponse toDto(Subscription s) {
        if (s == null) return null;
        com.gymtracker.dto.SubscriptionResponse resp = new com.gymtracker.dto.SubscriptionResponse();
        resp.setId(s.getId());
        resp.setMemberId(s.getMemberId());
        resp.setPlanId(s.getPlanId());
        resp.setStartDate(s.getStartDate());
        resp.setEndDate(s.getEndDate());
        resp.setStatus(s.getStatus());
        resp.setAutoRenew(s.getAutoRenew());
        return resp;
    }
}
