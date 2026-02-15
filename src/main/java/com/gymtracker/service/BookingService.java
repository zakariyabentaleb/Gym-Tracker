package com.gymtracker.service;

import com.gymtracker.dto.BookingCreateRequest;
import com.gymtracker.entity.Booking;
import com.gymtracker.entity.CourseSchedule;
import com.gymtracker.entity.Member;
import com.gymtracker.entity.Waitlist;
import com.gymtracker.mapper.BookingMapper;
import com.gymtracker.repository.BookingRepository;
import com.gymtracker.repository.CourseRepository;
import com.gymtracker.repository.CourseScheduleRepository;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.WaitlistRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourseScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final WaitlistRepository waitlistRepository;

    public BookingService(BookingRepository bookingRepository,
                          CourseScheduleRepository scheduleRepository,
                          CourseRepository courseRepository,
                          MemberRepository memberRepository,
                          WaitlistRepository waitlistRepository) {
        this.bookingRepository = bookingRepository;
        this.scheduleRepository = scheduleRepository;
        this.courseRepository = courseRepository;
        this.memberRepository = memberRepository;
        this.waitlistRepository = waitlistRepository;
    }

    public com.gymtracker.dto.BookingResponse create(BookingCreateRequest req) {
        if (req.getMemberId() == null) throw new IllegalArgumentException("memberId is required");
        Optional<Member> mOpt = memberRepository.findById(req.getMemberId());
        if (mOpt.isEmpty()) throw new IllegalArgumentException("Member not found: " + req.getMemberId());

        Optional<CourseSchedule> sOpt = scheduleRepository.findById(req.getScheduleId());
        if (sOpt.isEmpty()) throw new IllegalArgumentException("Schedule not found: " + req.getScheduleId());
        CourseSchedule s = sOpt.get();

        // check course exists
        Optional<com.gymtracker.entity.Course> courseOpt = courseRepository.findById(s.getCourseId());
        if (courseOpt.isEmpty()) throw new IllegalArgumentException("Course not found for schedule: " + s.getCourseId());
        com.gymtracker.entity.Course course = courseOpt.get();

        // check if member already booked
        Optional<Booking> existing = bookingRepository.findByScheduleIdAndMemberId(s.getId(), req.getMemberId());
        if (existing.isPresent() && !"CANCELLED".equalsIgnoreCase(existing.get().getStatus())) {
            throw new IllegalStateException("Member already has a booking for this schedule");
        }

        // determine capacity
        int capacity = s.getCapacity() != null ? s.getCapacity() : (course.getCapacity() != null ? course.getCapacity() : Integer.MAX_VALUE);

        // count confirmed bookings for this schedule
        List<Booking> bookings = bookingRepository.findByScheduleId(s.getId());
        long confirmed = bookings.stream().filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus())).count();

        if (confirmed < capacity) {
            Booking b = Booking.builder()
                    .scheduleId(s.getId())
                    .memberId(req.getMemberId())
                    .status("CONFIRMED")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            Booking saved = bookingRepository.save(b);
            return BookingMapper.toDto(saved);
        } else {
            // add to waitlist
            Integer maxPos = waitlistRepository.findMaxPositionForSchedule(s.getId());
            int pos = (maxPos != null ? maxPos : 0) + 1;
            Waitlist w = Waitlist.builder()
                    .scheduleId(s.getId())
                    .memberId(req.getMemberId())
                    .position(pos)
                    .createdAt(LocalDateTime.now())
                    .build();
            Waitlist saved = waitlistRepository.save(w);
            // return a BookingResponse-like object indicating waiting
            com.gymtracker.dto.BookingResponse resp = new com.gymtracker.dto.BookingResponse();
            resp.setId(null);
            resp.setScheduleId(s.getId());
            resp.setMemberId(req.getMemberId());
            resp.setStatus("WAITING");
            resp.setCreatedAt(saved.getCreatedAt());
            resp.setUpdatedAt(null);
            return resp;
        }
    }

    public com.gymtracker.dto.BookingResponse findById(Long id) {
        Optional<Booking> opt = bookingRepository.findById(id);
        if (opt.isEmpty()) return null;
        return BookingMapper.toDto(opt.get());
    }

    public List<com.gymtracker.dto.BookingResponse> findByScheduleId(Long scheduleId) {
        List<Booking> list = bookingRepository.findByScheduleId(scheduleId);
        List<com.gymtracker.dto.BookingResponse> out = new ArrayList<>();
        for (Booking b : list) out.add(BookingMapper.toDto(b));
        return out;
    }

    public List<com.gymtracker.dto.BookingResponse> findByMemberId(Long memberId) {
        List<Booking> list = bookingRepository.findByMemberId(memberId);
        List<com.gymtracker.dto.BookingResponse> out = new ArrayList<>();
        for (Booking b : list) out.add(BookingMapper.toDto(b));
        return out;
    }

    public com.gymtracker.dto.BookingResponse cancel(Long bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isEmpty()) throw new IllegalArgumentException("Booking not found: " + bookingId);
        Booking b = opt.get();
        if ("CANCELLED".equalsIgnoreCase(b.getStatus())) throw new IllegalStateException("Booking already cancelled");
        b.setStatus("CANCELLED");
        b.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(b);

        // promote first on waitlist if any
        promoteFromWaitlist(b.getScheduleId());

        return BookingMapper.toDto(b);
    }

    private void promoteFromWaitlist(Long scheduleId) {
        List<Waitlist> wl = waitlistRepository.findByScheduleId(scheduleId);
        if (wl == null || wl.isEmpty()) return;
        Waitlist first = wl.get(0);
        // create booking for this member
        Booking nb = Booking.builder()
                .scheduleId(scheduleId)
                .memberId(first.getMemberId())
                .status("CONFIRMED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        bookingRepository.save(nb);
        // remove waitlist entry
        waitlistRepository.deleteById(first.getId());
        // re-order positions: simple approach - fetch remaining and update positions
        List<Waitlist> remaining = waitlistRepository.findByScheduleId(scheduleId);
        int pos = 1;
        for (Waitlist w : remaining) {
            w.setPosition(pos++);
            waitlistRepository.save(w);
        }
    }
}

