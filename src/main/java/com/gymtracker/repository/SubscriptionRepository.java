package com.gymtracker.repository;

import com.gymtracker.entity.Subscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query(value = "SELECT * FROM subscriptions WHERE member_id = :memberId", nativeQuery = true)
    List<Subscription> findByMemberId(@Param("memberId") Long memberId);

    @Query(value = "SELECT * FROM subscriptions ORDER BY id DESC", nativeQuery = true)
    List<Subscription> findAllOrderByIdDesc();
}
