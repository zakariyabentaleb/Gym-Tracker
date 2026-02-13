package com.gymtracker.dto;

import lombok.Data;

@Data
public class SubscriptionUpdateRequest {
    private String status; // expected values: ACTIVE, EXPIRED, CANCELLED, SUSPENDED
    private Boolean autoRenew;
}

