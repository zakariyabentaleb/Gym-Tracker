package com.gymtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentCreateRequest {
    private Long subscriptionId;

    @NotNull(message = "memberId is required")
    private Long memberId;

    @NotNull(message = "amountCents is required")
    private Integer amountCents;

    @NotBlank(message = "method is required")
    private String method; // e.g., CASH, CARD, TRANSFER

    private String reference; // optional external reference
}
