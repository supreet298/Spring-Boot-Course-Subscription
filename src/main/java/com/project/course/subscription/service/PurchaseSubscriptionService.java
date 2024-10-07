package com.project.course.subscription.service;

import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.model.PurchaseSubscription;
import java.util.List;

public interface PurchaseSubscriptionService {

    PurchaseSubscription createPurchaseSubscription(PurchaseSubscription purchaseSubscription);

    List<PurchaseSubscriptionDTO> getAllPurchaseSubscriptions();
}
