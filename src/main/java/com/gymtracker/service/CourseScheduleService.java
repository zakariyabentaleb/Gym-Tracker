package com.gymtracker.service;

import com.gymtracker.entity.CourseSchedule;
import com.gymtracker.mapper.CourseScheduleMapper;
import com.gymtracker.repository.CourseRepository;
import com.gymtracker.repository.CourseScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseScheduleService {

    private final CourseScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;

    public CourseScheduleService(CourseScheduleRepository scheduleRepository, CourseRepository courseRepository) {
        this.scheduleRepository = scheduleRepository;
        this.courseRepository = courseRepository;
    }

    public com.gymtracker.dto.CourseScheduleResponse create(com.gymtracker.dto.CourseScheduleCreateRequest req) {
        // validate course exists
        Optional<com.gymtracker.entity.Course> courseOpt = courseRepository.findById(req.getCourseId());
        if (courseOpt.isEmpty()) throw new IllegalArgumentException("Course not found: " + req.getCourseId());
        // validate times
        if (req.getStartTime() == null || req.getEndTime() == null) {
            throw new IllegalArgumentException("startTime and endTime are required");
        }
        if (!req.getStartTime().isBefore(req.getEndTime())) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
        // validate capacity if provided
        if (req.getCapacity() != null && req.getCapacity() <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        CourseSchedule s = CourseScheduleMapper.fromCreateRequest(req);
        CourseSchedule saved = scheduleRepository.save(s);
        return CourseScheduleMapper.toDto(saved);
    }

    public List<com.gymtracker.dto.CourseScheduleResponse> findAll() {
        List<com.gymtracker.dto.CourseScheduleResponse> out = new ArrayList<>();
        scheduleRepository.findAll().forEach(s -> out.add(CourseScheduleMapper.toDto(s)));
        return out;
    }

    public com.gymtracker.dto.CourseScheduleResponse findById(Long id) {
        Optional<CourseSchedule> opt = scheduleRepository.findById(id);
        if (opt.isEmpty()) return null;
        return CourseScheduleMapper.toDto(opt.get());
    }

    public List<com.gymtracker.dto.CourseScheduleResponse> findByCourseId(Long courseId) {
        List<CourseSchedule> list = scheduleRepository.findByCourseId(courseId);
        List<com.gymtracker.dto.CourseScheduleResponse> out = new ArrayList<>();
        for (CourseSchedule s : list) out.add(CourseScheduleMapper.toDto(s));
        return out;
    }

    public com.gymtracker.dto.CourseScheduleResponse update(Long id, com.gymtracker.dto.CourseScheduleCreateRequest req) {
        Optional<CourseSchedule> opt = scheduleRepository.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("Schedule not found: " + id);
        CourseSchedule s = opt.get();
        if (req.getCoachId() != null) s.setCoachId(req.getCoachId());
        if (req.getRoom() != null) s.setRoom(req.getRoom());
        if (req.getStartTime() != null) s.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) s.setEndTime(req.getEndTime());
        // if both start and end (new or existing) are present, validate ordering
        if (s.getStartTime() == null || s.getEndTime() == null) {
            // allow partial update if one side is present? enforce presence
            throw new IllegalArgumentException("Both startTime and endTime must be set for a schedule");
        }
        if (!s.getStartTime().isBefore(s.getEndTime())) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
        if (req.getCapacity() != null) s.setCapacity(req.getCapacity());
        if (req.getActive() != null) s.setActive(req.getActive());
        CourseSchedule saved = scheduleRepository.save(s);
        return CourseScheduleMapper.toDto(saved);
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }
}
