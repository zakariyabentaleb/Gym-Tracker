package com.gymtracker.repository;

import com.gymtracker.entity.Coach;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoachRepository extends CrudRepository<Coach, Long> {
    @Query("SELECT * FROM coaches WHERE active = true")
    List<Coach> findActive();

    @Query("SELECT * FROM coaches WHERE user_id = :userId LIMIT 1")
    Optional<Coach> findByUserId(Long userId);
}

