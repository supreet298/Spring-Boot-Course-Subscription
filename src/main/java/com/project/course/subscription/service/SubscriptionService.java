package com.project.course.subscription.service;

import com.project.course.subscription.dto.SubscriptionDTO;
import com.project.course.subscription.model.Subscription;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

public interface SubscriptionService {

    Subscription createSubscription(@Valid Subscription subscription);

    Optional<SubscriptionDTO> getSubscriptionDTOByUuid(String uuid);

    Subscription updateSubscription(String uuid, Subscription subscription);

    boolean deleteSubscription(String uuid);

    Subscription getSubscriptionByUuid(String uuid);

    List<SubscriptionDTO> getAllActiveSubscriptionList();

    List<SubscriptionDTO> searchSubscription(String keyword);

    Page<SubscriptionDTO> getAllPaginatedAndSortedSubscription(int page, int size, String sortBy, String direction);
}