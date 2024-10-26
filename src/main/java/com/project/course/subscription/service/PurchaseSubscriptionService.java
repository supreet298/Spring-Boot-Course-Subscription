package com.project.course.subscription.service;

import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface PurchaseSubscriptionService {

    PurchaseSubscriptionDTO createPurchaseSubscription(@Valid PurchaseSubscriptionDTO purchaseSubscriptionDTO);

    List<PurchaseSubscriptionDTO> getAllPurchaseSubscriptions();

    Optional<PurchaseSubscriptionDTO> getPurchaseSubscriptionByUuid(String uuid);

    boolean deletePurchaseSubscription(String uuid);
}
