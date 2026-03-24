package com.gymtracker.repository;

import com.gymtracker.entity.Waitlist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    @Query(value = "SELECT * FROM waitlists WHERE schedule_id = :scheduleId ORDER BY position ASC", nativeQuery = true)
    List<Waitlist> findByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query(value = "SELECT COALESCE(MAX(position),0) FROM waitlists WHERE schedule_id = :scheduleId", nativeQuery = true)
    Integer findMaxPositionForSchedule(@Param("scheduleId") Long scheduleId);

    @Query(value = "SELECT * FROM waitlists WHERE member_id = :memberId ORDER BY created_at DESC", nativeQuery = true)
    List<Waitlist> findByMemberId(@Param("memberId") Long memberId);
}
