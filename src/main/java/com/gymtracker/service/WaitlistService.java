package com.gymtracker.service;

import com.gymtracker.dto.WaitlistResponse;
import com.gymtracker.entity.Booking;
import com.gymtracker.entity.CourseSchedule;
import com.gymtracker.entity.Member;
import com.gymtracker.entity.Waitlist;
import com.gymtracker.mapper.WaitlistMapper;
import com.gymtracker.repository.BookingRepository;
import com.gymtracker.repository.CourseRepository;
import com.gymtracker.repository.CourseScheduleRepository;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.WaitlistRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final MemberRepository memberRepository;
    private final CourseScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;
    private final BookingRepository bookingRepository;

    public WaitlistService(WaitlistRepository waitlistRepository,
                           MemberRepository memberRepository,
                           CourseScheduleRepository scheduleRepository,
                           CourseRepository courseRepository,
                           BookingRepository bookingRepository) {
        this.waitlistRepository = waitlistRepository;
        this.memberRepository = memberRepository;
        this.scheduleRepository = scheduleRepository;
        this.courseRepository = courseRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public WaitlistResponse addToWaitlist(Long scheduleId, Long memberId) {
        if (memberId == null) throw new IllegalArgumentException("memberId is required");
        Optional<Member> mOpt = memberRepository.findById(memberId);
        if (mOpt.isEmpty()) throw new IllegalArgumentException("Member not found: " + memberId);

        Optional<CourseSchedule> sOpt = scheduleRepository.findById(scheduleId);
        if (sOpt.isEmpty()) throw new IllegalArgumentException("Schedule not found: " + scheduleId);

        // check existing booking
        Optional<Booking> existing = bookingRepository.findByScheduleIdAndMemberId(scheduleId, memberId);
        if (existing.isPresent() && !BookingStatus.CANCELLED.equalsIgnoreCase(existing.get().getStatus())) {
            throw new IllegalStateException("Member already has a booking for this schedule");
        }

        // prevent duplicate waitlist entry
        List<Waitlist> current = waitlistRepository.findByScheduleId(scheduleId);
        for (Waitlist w : current) {
            if (w.getMemberId().equals(memberId)) {
                return WaitlistMapper.toDto(w);
            }
        }

        Integer maxPos = waitlistRepository.findMaxPositionForSchedule(scheduleId);
        int pos = (maxPos != null ? maxPos : 0) + 1;
        Waitlist w = Waitlist.builder()
                .scheduleId(scheduleId)
                .memberId(memberId)
                .position(pos)
                .createdAt(LocalDateTime.now())
                .build();
        
        try {
            Waitlist saved = waitlistRepository.save(w);
            return WaitlistMapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            // This happens when the member is already on the waitlist (UNIQUE constraint violation)
            // This can happen due to race conditions even with the check above
            throw new IllegalStateException("Vous êtes déjà en attente pour cette séance");
        }
    }

    public List<WaitlistResponse> listBySchedule(Long scheduleId) {
        List<Waitlist> list = waitlistRepository.findByScheduleId(scheduleId);
        List<WaitlistResponse> out = new ArrayList<>();
        for (Waitlist w : list) out.add(WaitlistMapper.toDto(w));
        return out;
    }

    public List<WaitlistResponse> listByMember(Long memberId) {
        List<Waitlist> list = waitlistRepository.findByMemberId(memberId);
        List<WaitlistResponse> out = new ArrayList<>();
        for (Waitlist w : list) out.add(WaitlistMapper.toDto(w));
        return out;
    }

    @Transactional
    public void remove(Long id) {
        waitlistRepository.deleteById(id);
        // optionally re-order positions for schedule
        // fetch scheduleId to reindex
        // naive approach: try to read remaining entries for the schedule if possible
    }

    @Transactional
    public WaitlistResponse confirm(Long id, Long memberId) {
        Optional<Waitlist> opt = waitlistRepository.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("Waitlist entry not found: " + id);
        Waitlist w = opt.get();
        if (!w.getMemberId().equals(memberId)) throw new IllegalArgumentException("Member mismatch");

        Long scheduleId = w.getScheduleId();
        Optional<CourseSchedule> sOpt = scheduleRepository.findById(scheduleId);
        if (sOpt.isEmpty()) throw new IllegalArgumentException("Schedule not found: " + scheduleId);
        CourseSchedule s = sOpt.get();

        // check capacity
        Optional<com.gymtracker.entity.Course> courseOpt = courseRepository.findById(s.getCourseId());
        if (courseOpt.isEmpty()) throw new IllegalArgumentException("Course not found for schedule: " + s.getCourseId());
        com.gymtracker.entity.Course course = courseOpt.get();
        int capacity = s.getCapacity() != null ? s.getCapacity() : (course.getCapacity() != null ? course.getCapacity() : Integer.MAX_VALUE);
        List<Booking> bookings = bookingRepository.findByScheduleId(scheduleId);
        long confirmed = bookings.stream().filter(b -> BookingStatus.CONFIRMED.equalsIgnoreCase(b.getStatus())).count();
        if (confirmed >= capacity) {
            throw new IllegalStateException("No capacity available to confirm booking");
        }

        Booking nb = Booking.builder()
                .scheduleId(scheduleId)
                .memberId(memberId)
                .status(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        try {
            bookingRepository.save(nb);
        } catch (DataIntegrityViolationException ex) {
            // someone else made booking
        }
        // remove waitlist entry
        waitlistRepository.deleteById(id);
        // reindex remaining
        List<Waitlist> remaining = waitlistRepository.findByScheduleId(scheduleId);
        int pos = 1;
        for (Waitlist rem : remaining) {
            rem.setPosition(pos++);
            waitlistRepository.save(rem);
        }
        return WaitlistMapper.toDto(w);
    }
}
