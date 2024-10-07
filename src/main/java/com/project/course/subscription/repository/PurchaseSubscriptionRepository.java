package com.project.course.subscription.repository;

import com.project.course.subscription.model.PurchaseSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseSubscriptionRepository extends JpaRepository<PurchaseSubscription,Long> {

    Optional<PurchaseSubscription> findByUuid(String uuid);

    @Query("SELECT ps FROM PurchaseSubscription ps WHERE ps.paxUser.id = :userId AND ps.expiryDate > CURRENT_TIMESTAMP AND ps.isActive = true")
    List<PurchaseSubscription> findActiveSubscriptionsByUserId(@Param("userId") Long userId);
}
