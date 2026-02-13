package com.gymtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private Long id;
    private Long memberId;
    private Long planId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Boolean autoRenew;
}
