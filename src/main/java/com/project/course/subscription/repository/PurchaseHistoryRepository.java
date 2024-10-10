package com.project.course.subscription.repository;

import com.project.course.subscription.model.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory,Long> {

    @Query("SELECT ph FROM PurchaseHistory ph WHERE ph.paxUserHead.id = :userId AND ph.subscription.id = :subscriptionId")
    List<PurchaseHistory> findPurchaseHistoryByUserAndSubscription(@Param("userId") Long userId, @Param("subscriptionId") Long subscriptionId);
}
