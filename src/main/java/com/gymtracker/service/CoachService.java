package com.gymtracker.service;

import com.gymtracker.dto.CoachCreateRequest;
import com.gymtracker.dto.CoachResponse;
import com.gymtracker.entity.Coach;
import com.gymtracker.mapper.CoachMapper;
import com.gymtracker.mapper.CourseScheduleMapper;
import com.gymtracker.repository.CoachRepository;
import com.gymtracker.repository.CourseScheduleRepository;
import com.gymtracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CoachService {

    private final CoachRepository coachRepository;
    private final CourseScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public CoachService(CoachRepository coachRepository, CourseScheduleRepository scheduleRepository, UserRepository userRepository) {
        this.coachRepository = coachRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    public CoachResponse create(CoachCreateRequest req) {
        Coach c = CoachMapper.toEntity(req);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        Coach saved = coachRepository.save(c);
        return CoachMapper.toDto(saved);
    }

    public CoachResponse getById(Long id) {
        Optional<Coach> opt = coachRepository.findById(id);
        return opt.map(CoachMapper::toDto).orElse(null);
    }

    public List<CoachResponse> listActive() {
        List<Coach> list = coachRepository.findActive();
        List<CoachResponse> out = new ArrayList<>();
        for (Coach c : list) out.add(CoachMapper.toDto(c));
        return out;
    }

    @Transactional
    public CoachResponse update(Long id, CoachCreateRequest req) {
        Optional<Coach> opt = coachRepository.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("Coach not found: " + id);
        Coach c = opt.get();
        if (req.getDisplayName() != null) c.setDisplayName(req.getDisplayName());
        if (req.getPhone() != null) c.setPhone(req.getPhone());
        if (req.getBio() != null) c.setBio(req.getBio());
        if (req.getCertifications() != null) c.setCertifications(req.getCertifications());
        if (req.getPhotoUrl() != null) c.setPhotoUrl(req.getPhotoUrl());
        if (req.getUserId() != null) c.setUserId(req.getUserId());
        c.setUpdatedAt(LocalDateTime.now());
        Coach saved = coachRepository.save(c);
        return CoachMapper.toDto(saved);
    }

    public void delete(Long id) {
        coachRepository.deleteById(id);
    }

    @Transactional
    public void assignToSchedule(Long scheduleId, Long coachId) {
        Optional<com.gymtracker.entity.CourseSchedule> opt = scheduleRepository.findById(scheduleId);
        if (opt.isEmpty()) throw new IllegalArgumentException("Schedule not found: " + scheduleId);
        com.gymtracker.entity.CourseSchedule s = opt.get();
        s.setCoachId(coachId);
        scheduleRepository.save(s);
    }

    /**
     * Return course schedules for the currently authenticated user (must have a Coach profile).
     */
    public List<com.gymtracker.dto.CourseScheduleResponse> getSchedulesForUser(String username) {
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) throw new IllegalArgumentException("User not found: " + username);
        Long userId = userOpt.get().getId();
        Optional<Coach> coachOpt = coachRepository.findByUserId(userId);
        if (coachOpt.isEmpty()) throw new IllegalArgumentException("No coach profile found for user: " + username);
        Long coachId = coachOpt.get().getId();
        List<com.gymtracker.entity.CourseSchedule> list = scheduleRepository.findByCoachId(coachId);
        List<com.gymtracker.dto.CourseScheduleResponse> out = new ArrayList<>();
        for (com.gymtracker.entity.CourseSchedule s : list) {
            out.add(CourseScheduleMapper.toDto(s));
        }
        return out;
    }

    /**
     * Return course schedules for the coach identified by coachId (admin/receptionist use).
     */
    public List<com.gymtracker.dto.CourseScheduleResponse> getSchedulesForCoachId(Long coachId) {
        List<com.gymtracker.entity.CourseSchedule> list = scheduleRepository.findByCoachId(coachId);
        List<com.gymtracker.dto.CourseScheduleResponse> out = new ArrayList<>();
        for (com.gymtracker.entity.CourseSchedule s : list) {
            out.add(CourseScheduleMapper.toDto(s));
        }
        return out;
    }
}
