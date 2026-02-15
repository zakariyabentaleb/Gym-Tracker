package com.gymtracker.repository;

import com.gymtracker.entity.Waitlist;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitlistRepository extends CrudRepository<Waitlist, Long> {

    @Query("SELECT * FROM waitlists WHERE schedule_id = :scheduleId ORDER BY position ASC")
    List<Waitlist> findByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT COALESCE(MAX(position),0) FROM waitlists WHERE schedule_id = :scheduleId")
    Integer findMaxPositionForSchedule(@Param("scheduleId") Long scheduleId);
}

