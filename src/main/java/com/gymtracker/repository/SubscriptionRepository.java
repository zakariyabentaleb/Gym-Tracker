package com.gymtracker.repository;

import com.gymtracker.entity.Subscription;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

    @Query("SELECT * FROM subscriptions WHERE member_id = :memberId")
    List<Subscription> findByMemberId(@Param("memberId") Long memberId);
}
