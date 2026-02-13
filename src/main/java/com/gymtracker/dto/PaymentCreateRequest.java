package com.gymtracker.dto;

import lombok.Data;

@Data
public class PaymentCreateRequest {
    private Long subscriptionId;
    private Long memberId;
    private Integer amountCents;
    private String method; // e.g., CASH, CARD, TRANSFER
    private String reference; // optional external reference
}

