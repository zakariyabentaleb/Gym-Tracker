package com.gymtracker.repository;

import com.gymtracker.entity.CourseSchedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {

    @Query(value = "SELECT * FROM course_schedules WHERE course_id = :courseId", nativeQuery = true)
    List<CourseSchedule> findByCourseId(@Param("courseId") Long courseId);

    @Query(value = "SELECT * FROM course_schedules WHERE coach_id = :coachId", nativeQuery = true)
    List<CourseSchedule> findByCoachId(@Param("coachId") Long coachId);
}
