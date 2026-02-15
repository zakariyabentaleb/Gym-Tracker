package com.gymtracker.service;

import com.gymtracker.entity.Course;
import com.gymtracker.mapper.CourseMapper;
import com.gymtracker.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public com.gymtracker.dto.CourseResponse create(com.gymtracker.dto.CourseCreateRequest req) {
        Course c = CourseMapper.fromCreateRequest(req);
        Course saved = courseRepository.save(c);
        return CourseMapper.toDto(saved);
    }

    public com.gymtracker.dto.CourseResponse findById(Long id) {
        Optional<Course> opt = courseRepository.findById(id);
        if (opt.isEmpty()) return null;
        return CourseMapper.toDto(opt.get());
    }

    public List<com.gymtracker.dto.CourseResponse> findAll() {
        Iterable<Course> it = courseRepository.findAll();
        List<com.gymtracker.dto.CourseResponse> out = new ArrayList<>();
        for (Course c : it) out.add(CourseMapper.toDto(c));
        return out;
    }

    public com.gymtracker.dto.CourseResponse update(Long id, com.gymtracker.dto.CourseCreateRequest req) {
        Optional<Course> opt = courseRepository.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("Course not found: " + id);
        Course c = opt.get();
        if (req.getName() != null) c.setName(req.getName());
        if (req.getDescription() != null) c.setDescription(req.getDescription());
        if (req.getDurationMinutes() != null) c.setDurationMinutes(req.getDurationMinutes());
        if (req.getCapacity() != null) c.setCapacity(req.getCapacity());
        if (req.getActive() != null) c.setActive(req.getActive());
        Course saved = courseRepository.save(c);
        return CourseMapper.toDto(saved);
    }

    public void delete(Long id) {
        courseRepository.deleteById(id);
    }
}

