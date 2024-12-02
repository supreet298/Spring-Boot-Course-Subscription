package com.project.course.subscription.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.course.subscription.model.PurchaseSubscription;

@Repository
public interface PurchaseSubscriptionRepository extends JpaRepository<PurchaseSubscription,Long> {

    Optional<PurchaseSubscription> findByUuid(String uuid);

    @Query("SELECT ps FROM PurchaseSubscription ps WHERE ps.paxUser.id = :userId AND ps.expiryDate > CURRENT_TIMESTAMP AND ps.isActive = true")
    List<PurchaseSubscription> findActiveSubscriptionsByUserId(@Param("userId") Long userId);

    @Query("SELECT ps FROM PurchaseSubscription ps WHERE ps.expiryDate < :currentDate AND ps.recurring = true")
    List<PurchaseSubscription> findExpiredRecurringSubscriptions(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT ps FROM PurchaseSubscription ps WHERE ps.paxUser.uuid = :paxUserUuid AND ps.expiryDate > :currentDate")
    List<PurchaseSubscription> findActiveSubscriptionsByPaxUserUuid(@Param("paxUserUuid") String paxUserUuid, @Param("currentDate") LocalDateTime currentDate);
        
    @Query("SELECT ps FROM PurchaseSubscription ps WHERE ps.subscription.id = :subscriptionId AND ps.recurring = :recurring")
    List<PurchaseSubscription> findAllPaxUserIdsBySubscriptionIdAndRecurring(@Param("subscriptionId") Long subscriptionId, @Param("recurring") Boolean recurring);

  }
