package com.project.course.subscription.service;

import com.project.course.subscription.dto.SubscriptionDTO;
import com.project.course.subscription.model.Subscription;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface SubscriptionService {

    Subscription createSubscription(@Valid Subscription subscription);

    List<SubscriptionDTO> getAllActiveSubscriptions();

    Optional<SubscriptionDTO> getSubscriptionByUuid(String uuid);

    Optional<Subscription> updateSubscription(String uuid, Subscription subscription);

    boolean deleteSubscription(String uuid);

    Subscription getSubscriptionById(Long id);
}