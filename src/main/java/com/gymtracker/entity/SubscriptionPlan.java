package com.gymtracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("subscription_plans")
public class SubscriptionPlan {

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("duration_days")
    private Integer durationDays;

    @Column("price_cents")
    private Integer priceCents;

    @Column("includes_classes")
    private Boolean includesClasses;

    @Column("description")
    private String description;

    @Column("active")
    private Boolean active;
}

