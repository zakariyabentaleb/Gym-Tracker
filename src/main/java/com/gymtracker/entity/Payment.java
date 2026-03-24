package com.gymtracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "amount_cents")
    private Integer amountCents;

    @Column(name = "method")
    private String method;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "reference")
    private String reference;
}
