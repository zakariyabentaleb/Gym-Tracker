package com.gymtracker.repository;

import com.gymtracker.entity.Member;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Optional<Member> findByUserId(Long userId);

    @Query("""
            SELECT *
            FROM members
            WHERE (:q IS NULL OR :q = ''
                OR LOWER(first_name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(last_name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(phone) LIKE LOWER(CONCAT('%', :q, '%'))
            )
            ORDER BY id DESC
            LIMIT :limit OFFSET :offset
            """)
    List<Member> search(@Param("q") String q,
                        @Param("limit") long limit,
                        @Param("offset") long offset);

    @Query("""
            SELECT COUNT(*)
            FROM members
            WHERE (:q IS NULL OR :q = ''
                OR LOWER(first_name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(last_name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(phone) LIKE LOWER(CONCAT('%', :q, '%'))
            )
            """)
    long countSearch(@Param("q") String q);
}

