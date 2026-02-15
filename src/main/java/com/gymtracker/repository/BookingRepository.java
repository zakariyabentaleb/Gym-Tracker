package com.gymtracker.repository;

import com.gymtracker.entity.Booking;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Long> {

    @Query("SELECT * FROM bookings WHERE schedule_id = :scheduleId")
    List<Booking> findByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT * FROM bookings WHERE member_id = :memberId")
    List<Booking> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT * FROM bookings WHERE schedule_id = :scheduleId AND member_id = :memberId LIMIT 1")
    Optional<Booking> findByScheduleIdAndMemberId(@Param("scheduleId") Long scheduleId, @Param("memberId") Long memberId);
}
