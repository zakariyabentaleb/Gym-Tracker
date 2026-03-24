package com.gymtracker.repository;

import com.gymtracker.entity.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT * FROM bookings WHERE schedule_id = :scheduleId", nativeQuery = true)
    List<Booking> findByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query(value = "SELECT * FROM bookings WHERE member_id = :memberId", nativeQuery = true)
    List<Booking> findByMemberId(@Param("memberId") Long memberId);

    @Query(value = "SELECT * FROM bookings WHERE schedule_id = :scheduleId AND member_id = :memberId LIMIT 1", nativeQuery = true)
    Optional<Booking> findByScheduleIdAndMemberId(@Param("scheduleId") Long scheduleId, @Param("memberId") Long memberId);

    @Query(value = "SELECT COUNT(*) FROM bookings WHERE schedule_id = :scheduleId AND status = 'CONFIRMED'", nativeQuery = true)
    int countConfirmedByScheduleId(@Param("scheduleId") Long scheduleId);
}
