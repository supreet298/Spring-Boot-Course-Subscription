package com.project.course.subscription.repository;

import com.project.course.subscription.model.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {

    Page<Subscription> findByIsActiveTrue(Pageable pageable);

    List<Subscription> findByIsActiveTrue();

    Optional<Subscription> findByUuidAndIsActiveTrue(String uuid);

}
