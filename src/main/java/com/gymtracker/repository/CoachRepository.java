package com.gymtracker.repository;

import com.gymtracker.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    // Derived query methods for JPA
    List<Coach> findByActiveTrue();

    Optional<Coach> findByUserId(Long userId);
}
