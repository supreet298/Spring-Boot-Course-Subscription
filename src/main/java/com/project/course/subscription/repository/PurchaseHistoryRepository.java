package com.project.course.subscription.repository;

import com.project.course.subscription.model.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {

    @Query("SELECT ph FROM PurchaseHistory ph WHERE ph.paxUser.id = :userId AND ph.subscription.id = :subscriptionId")
    List<PurchaseHistory> findPurchaseHistoryByUserAndSubscription(@Param("userId") Long userId, @Param("subscriptionId") Long subscriptionId);

    // Custom query to find clients with plans expiring in exactly 5 days
    @Query("SELECT p FROM PurchaseHistory p WHERE p.expiryDate = :expiryDate")
    List<PurchaseHistory> findExpiringPlans(@Param("expiryDate") LocalDateTime expiryDate);

    @Query("SELECT p FROM PurchaseHistory p WHERE p.expiryDate BETWEEN :start AND :end")
    List<PurchaseHistory> findExpiringPlansBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<PurchaseHistory> findByPaxUser_Uuid(String uuid);

    List<PurchaseHistory> findByPaxUser_UuidAndPurchaseDateLessThanEqualAndExpiryDateGreaterThanEqual(String userUuid, LocalDateTime now, LocalDateTime now1);
}