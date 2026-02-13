package com.gymtracker.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SubscriptionCreateRequest {
    private Long memberId;
    private Long planId;
    private LocalDate startDate;
    private Boolean autoRenew = false;
}

