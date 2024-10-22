package com.project.course.subscription.service;

import com.project.course.subscription.dto.SubscriptionDTO;
import com.project.course.subscription.model.Subscription;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface SubscriptionService {

    void createSubscription(@Valid Subscription subscription);

    List<Subscription> getAllActiveSubscriptions();

    Optional<SubscriptionDTO> getSubscriptionByUuid(String uuid);

    void updateSubscription(String uuid, Subscription subscription);

    void deleteSubscription(String uuid);

    Subscription getSubscriptionById(Long id);
}