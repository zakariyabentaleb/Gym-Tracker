package com.gymtracker.controller;

import com.gymtracker.dto.MemberCreateRequest;
import com.gymtracker.dto.MemberResponse;
import com.gymtracker.dto.MemberUpdateRequest;
import com.gymtracker.dto.PagedResponse;
import com.gymtracker.repository.UserRepository;
import com.gymtracker.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final UserRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public MemberResponse create(@Valid @RequestBody MemberCreateRequest request) {
        return memberService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public PagedResponse<MemberResponse> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return memberService.search(q, page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public MemberResponse get(@PathVariable long id) {
        return memberService.getById(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public MemberResponse update(@PathVariable long id, @Valid @RequestBody MemberUpdateRequest request) {
        return memberService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_RECEPTIONIST')")
    public void delete(@PathVariable long id) {
        memberService.delete(id);
    }

    /**
     * For ROLE_MEMBER: return the Member record linked to the currently authenticated user.
     */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public MemberResponse me(Authentication authentication) {
        String username = authentication.getName();
        long userId = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username))
                .getId();
        return memberService.getByUserId(userId);
    }
}


