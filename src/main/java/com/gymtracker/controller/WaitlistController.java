package com.gymtracker.controller;

import com.gymtracker.dto.WaitlistCreateRequest;
import com.gymtracker.dto.WaitlistResponse;
import com.gymtracker.entity.AppUser;
import com.gymtracker.entity.Member;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.UserRepository;
import com.gymtracker.service.WaitlistService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class WaitlistController {

    private final WaitlistService waitlistService;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    @PostMapping("/schedules/{scheduleId}/waitlist")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public WaitlistResponse addToWaitlist(@PathVariable Long scheduleId, @Valid @RequestBody WaitlistCreateRequest req) {
        return waitlistService.addToWaitlist(scheduleId, req.getMemberId());
    }

    @PostMapping("/schedules/{scheduleId}/waitlist/me")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public WaitlistResponse addToWaitlistForMe(Authentication authentication, @PathVariable Long scheduleId) {
        if (authentication == null || authentication.getName() == null) throw new IllegalArgumentException("Not authenticated");
        String username = authentication.getName();
        Optional<AppUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) throw new IllegalArgumentException("User not found: " + username);
        Long userId = userOpt.get().getId();
        Optional<Member> memberOpt = memberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) throw new IllegalArgumentException("Member not found for user: " + userId);
        return waitlistService.addToWaitlist(scheduleId, memberOpt.get().getId());
    }

    @GetMapping("/schedules/{scheduleId}/waitlist")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public List<WaitlistResponse> listBySchedule(@PathVariable Long scheduleId) {
        return waitlistService.listBySchedule(scheduleId);
    }

    @GetMapping("/waitlist/me")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public List<WaitlistResponse> myWaitlists(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) throw new IllegalArgumentException("Not authenticated");
        String username = authentication.getName();
        Optional<AppUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) throw new IllegalArgumentException("User not found: " + username);
        Long userId = userOpt.get().getId();
        Optional<Member> memberOpt = memberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) throw new IllegalArgumentException("Member not found for user: " + userId);
        return waitlistService.listByMember(memberOpt.get().getId());
    }

    @DeleteMapping("/waitlist/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST','ROLE_MEMBER')")
    public void remove(@PathVariable Long id, Authentication authentication) {
        // If member, ensure ownership
        if (authentication != null && authentication.getAuthorities() != null) {
            boolean isMember = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"));
            boolean isAdminOrRec = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_RECEPTIONIST"));
            if (isMember && !isAdminOrRec) {
                String username = authentication.getName();
                Optional<AppUser> userOpt = userRepository.findByUsername(username);
                if (userOpt.isEmpty()) throw new IllegalArgumentException("User not found: " + username);
                Long userId = userOpt.get().getId();
                Optional<Member> memberOpt = memberRepository.findByUserId(userId);
                if (memberOpt.isEmpty()) throw new IllegalArgumentException("Member not found for user: " + userId);
                // verify ownership
                List<WaitlistResponse> list = waitlistService.listByMember(memberOpt.get().getId());
                boolean found = list.stream().anyMatch(w -> w.getId() != null && w.getId().equals(id));
                if (!found) throw new AccessDeniedException("Members can only remove their own waitlist entries");
            }
        }
        waitlistService.remove(id);
    }

    @PostMapping("/waitlist/{id}/confirm")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public WaitlistResponse confirm(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) throw new IllegalArgumentException("Not authenticated");
        String username = authentication.getName();
        Optional<AppUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) throw new IllegalArgumentException("User not found: " + username);
        Long userId = userOpt.get().getId();
        Optional<Member> memberOpt = memberRepository.findByUserId(userId);
        if (memberOpt.isEmpty()) throw new IllegalArgumentException("Member not found for user: " + userId);
        return waitlistService.confirm(id, memberOpt.get().getId());
    }
}

