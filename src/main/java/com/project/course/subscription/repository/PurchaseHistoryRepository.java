package com.project.course.subscription.repository;

import com.project.course.subscription.model.PurchaseHistory;
import com.project.course.subscription.model.PurchaseSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory,Long> {

    int countByPurchaseSubscription(PurchaseSubscription purchaseSubscription);
}
