package com.project.course.subscription.service;

import java.util.List;
import java.util.Optional;
import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.dto.PurchaseSubscriptionResponseDTO;
import jakarta.validation.Valid;

public interface PurchaseSubscriptionService {

    PurchaseSubscriptionDTO createPurchaseSubscription(@Valid PurchaseSubscriptionDTO purchaseSubscriptionDTO);

    List<PurchaseSubscriptionDTO> getAllPurchaseSubscriptions();

    Optional<PurchaseSubscriptionDTO> getPurchaseSubscriptionByUuid(String uuid);

    boolean disableRecurringForSubscription(String uuid);

    List<PurchaseSubscriptionResponseDTO> getActiveSubscriptionsByPaxUserUuid(String paxUserUuid);
}
