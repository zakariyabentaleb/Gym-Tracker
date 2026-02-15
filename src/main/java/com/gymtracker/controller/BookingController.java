package com.gymtracker.controller;

import com.gymtracker.dto.BookingCreateRequest;
import com.gymtracker.dto.BookingResponse;
import com.gymtracker.entity.AppUser;
import com.gymtracker.entity.Member;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.UserRepository;
import com.gymtracker.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public BookingResponse create(@Valid @RequestBody BookingCreateRequest req) {
        return bookingService.create(req);
    }

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public BookingResponse createForMe(Authentication authentication, @Valid @RequestBody BookingCreateRequest req) {
        if (authentication == null || authentication.getName() == null) throw new IllegalArgumentException("Not authenticated");
        String username = authentication.getName();
        Optional<AppUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) throw new IllegalArgumentException("User not found: " + username);
        Long userId = userOpt.get().getId();
        Optional<Member> memberOpt = memberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) throw new IllegalArgumentException("Member not found for user: " + userId);
        req.setMemberId(memberOpt.get().getId());
        return bookingService.create(req);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public BookingResponse getById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @GetMapping("/schedule/{scheduleId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public List<BookingResponse> listBySchedule(@PathVariable Long scheduleId) {
        return bookingService.findByScheduleId(scheduleId);
    }

    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public List<BookingResponse> listByMember(@PathVariable Long memberId) {
        return bookingService.findByMemberId(memberId);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public List<BookingResponse> myBookings(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) throw new IllegalArgumentException("Not authenticated");
        String username = authentication.getName();
        Optional<AppUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) throw new IllegalArgumentException("User not found: " + username);
        Long userId = userOpt.get().getId();
        Optional<Member> memberOpt = memberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) throw new IllegalArgumentException("Member not found for user: " + userId);
        return bookingService.findByMemberId(memberOpt.get().getId());
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST','ROLE_MEMBER')")
    public BookingResponse cancel(@PathVariable Long id, Authentication authentication) {
        // If member is cancelling, ensure they own the booking
        if (authentication != null && authentication.getAuthorities() != null) {
            boolean isMember = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"));
            boolean isAdminOrRec = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_RECEPTIONIST"));
            if (isMember && !isAdminOrRec) {
                // verify ownership
                String username = authentication.getName();
                Optional<AppUser> userOpt = userRepository.findByUsername(username);
                if (userOpt.isEmpty()) throw new IllegalArgumentException("User not found: " + username);
                Long userId = userOpt.get().getId();
                Optional<Member> memberOpt = memberRepository.findByUserId(userId);
                if (memberOpt.isEmpty()) throw new IllegalArgumentException("Member not found for user: " + userId);
                // check booking belongs to member
                com.gymtracker.dto.BookingResponse resp = bookingService.findById(id);
                if (resp == null) throw new IllegalArgumentException("Booking not found: " + id);
                if (!memberOpt.get().getId().equals(resp.getMemberId())) {
                    throw new AccessDeniedException("Members can only cancel their own bookings");
                }
            }
        }
        return bookingService.cancel(id);
    }
}

