package com.project.course.subscription.repository;

import com.project.course.subscription.model.PurchaseSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PurchaseSubscriptionRepository extends JpaRepository<PurchaseSubscription,Long> {

    Optional<PurchaseSubscription> findByUuid(String uuid);
}
