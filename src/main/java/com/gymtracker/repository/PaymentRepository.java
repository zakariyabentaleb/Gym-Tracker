package com.gymtracker.repository;

import com.gymtracker.entity.Payment;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    @Query("SELECT * FROM payments WHERE member_id = :memberId")
    List<Payment> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT * FROM payments WHERE subscription_id = :subscriptionId")
    List<Payment> findBySubscriptionId(@Param("subscriptionId") Long subscriptionId);
}

