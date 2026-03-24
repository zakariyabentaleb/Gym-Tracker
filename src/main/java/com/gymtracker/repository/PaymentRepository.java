package com.gymtracker.repository;

import com.gymtracker.entity.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = "SELECT * FROM payments WHERE member_id = :memberId", nativeQuery = true)
    List<Payment> findByMemberId(@Param("memberId") Long memberId);

    @Query(value = "SELECT * FROM payments WHERE subscription_id = :subscriptionId", nativeQuery = true)
    List<Payment> findBySubscriptionId(@Param("subscriptionId") Long subscriptionId);

    @Query(value = "SELECT * FROM payments ORDER BY payment_date DESC", nativeQuery = true)
    List<Payment> findAllOrderByPaymentDateDesc();
}
