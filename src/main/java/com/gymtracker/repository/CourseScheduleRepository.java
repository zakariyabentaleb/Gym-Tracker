package com.gymtracker.repository;

import com.gymtracker.entity.CourseSchedule;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseScheduleRepository extends CrudRepository<CourseSchedule, Long> {
    @Query("SELECT * FROM course_schedules WHERE course_id = :courseId")
    List<CourseSchedule> findByCourseId(@Param("courseId") Long courseId);
}

