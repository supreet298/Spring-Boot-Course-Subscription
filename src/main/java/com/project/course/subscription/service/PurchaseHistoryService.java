package com.project.course.subscription.service;

import com.project.course.subscription.model.PurchaseHistory;
import java.util.List;

public interface PurchaseHistoryService {

    List<PurchaseHistory> getAllPurchaseSubscriptionHistory();

    void createPurchaseHistory(PurchaseHistory purchaseHistory);
}
