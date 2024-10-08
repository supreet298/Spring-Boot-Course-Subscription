package com.project.course.subscription.service;

import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.model.PurchaseSubscription;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface PurchaseSubscriptionService {

    PurchaseSubscription createPurchaseSubscription(@Valid PurchaseSubscription purchaseSubscription);

    List<PurchaseSubscriptionDTO> getAllPurchaseSubscriptions();

    Optional<PurchaseSubscriptionDTO> getPurchaseSubscriptionByUuid(String uuid);

    boolean deletePurchaseSubscription(String uuid);
}
