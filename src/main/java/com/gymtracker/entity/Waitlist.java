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
@Table("waitlists")
public class Waitlist {

    @Id
    @Column("id")
    private Long id;

    @Column("schedule_id")
    private Long scheduleId;

    @Column("member_id")
    private Long memberId;

    @Column("position")
    private Integer position;

    @Column("created_at")
    private LocalDateTime createdAt;
}

