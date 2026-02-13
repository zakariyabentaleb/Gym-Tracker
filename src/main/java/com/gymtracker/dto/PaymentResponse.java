package com.gymtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long subscriptionId;
    private Long memberId;
    private Integer amountCents;
    private String method;
    private String status;
    private LocalDateTime paymentDate;
    private String reference;
}

