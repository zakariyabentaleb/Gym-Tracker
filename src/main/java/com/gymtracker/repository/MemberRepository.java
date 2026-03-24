package com.gymtracker.repository;

import com.gymtracker.entity.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(Long userId);

    @Query(value = """
            SELECT *
            FROM members
            WHERE (:q IS NULL OR :q = ''
                OR LOWER(first_name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(last_name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(phone) LIKE LOWER(CONCAT('%', :q, '%'))
            )
            ORDER BY id DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Member> search(@Param("q") String q,
                        @Param("limit") long limit,
                        @Param("offset") long offset);

    @Query(value = """
            SELECT COUNT(*)
            FROM members
            WHERE (:q IS NULL OR :q = ''
                OR LOWER(first_name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(last_name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(phone) LIKE LOWER(CONCAT('%', :q, '%'))
            )
            """, nativeQuery = true)
    long countSearch(@Param("q") String q);
}
