package com.gymtracker.service;

import com.gymtracker.dto.MemberCreateRequest;
import com.gymtracker.dto.MemberResponse;
import com.gymtracker.dto.MemberUpdateRequest;
import com.gymtracker.dto.PagedResponse;
import com.gymtracker.entity.Member;
import com.gymtracker.repository.MemberRepository;
import com.gymtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    @Transactional
    public MemberResponse create(MemberCreateRequest request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("User not found: " + request.getUserId());
        }
        if (memberRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Member already exists for userId: " + request.getUserId());
        }

        Member saved = memberRepository.save(Member.builder()
                .userId(request.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .build());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public MemberResponse getById(long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + id));
        return toResponse(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getByUserId(long userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found for userId: " + userId));
        return toResponse(member);
    }

    @Transactional(readOnly = true)
    public PagedResponse<MemberResponse> search(String q, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));
        long offset = (long) safePage * safeSize;

        List<MemberResponse> items = memberRepository.search(q, safeSize, offset).stream()
                .map(this::toResponse)
                .toList();
        long total = memberRepository.countSearch(q);

        return new PagedResponse<>(items, total, safePage, safeSize);
    }

    @Transactional
    public MemberResponse update(long id, MemberUpdateRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + id));

        if (request.getFirstName() != null) member.setFirstName(request.getFirstName());
        if (request.getLastName() != null) member.setLastName(request.getLastName());
        if (request.getPhone() != null) member.setPhone(request.getPhone());
        if (request.getBirthDate() != null) member.setBirthDate(request.getBirthDate());
        if (request.getActive() != null) member.setActive(request.getActive());

        return toResponse(memberRepository.save(member));
    }

    @Transactional
    public void delete(long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Member not found: " + id);
        }
        memberRepository.deleteById(id);
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getUserId(),
                member.getFirstName(),
                member.getLastName(),
                member.getPhone(),
                member.getBirthDate(),
                member.getActive()
        );
    }
}

