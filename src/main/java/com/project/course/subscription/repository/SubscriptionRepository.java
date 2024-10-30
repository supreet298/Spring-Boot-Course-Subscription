package com.project.course.subscription.repository;

import com.project.course.subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {

    List<Subscription> findByIsActiveTrue();

    Optional<Subscription> findByUuidAndIsActiveTrue(String uuid);

}
