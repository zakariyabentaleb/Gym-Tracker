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
@Table("course_schedules")
public class CourseSchedule {

    @Id
    @Column("id")
    private Long id;

    @Column("course_id")
    private Long courseId;

    @Column("coach_id")
    private Long coachId;

    @Column("room")
    private String room;

    @Column("start_time")
    private LocalDateTime startTime;

    @Column("end_time")
    private LocalDateTime endTime;

    @Column("capacity")
    private Integer capacity; // optional override, null means use course.capacity

    @Column("active")
    private Boolean active;
}

