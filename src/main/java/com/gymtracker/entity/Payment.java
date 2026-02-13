package com.gymtracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("payments")
public class Payment {

    @Id
    @Column("id")
    private Long id;

    @Column("subscription_id")
    private Long subscriptionId;

    @Column("member_id")
    private Long memberId;

    @Column("amount_cents")
    private Integer amountCents;

    @Column("method")
    private String method;

    @Column("status")
    private String status;

    @Column("payment_date")
    private LocalDateTime paymentDate;

    @Column("reference")
    private String reference;
}

