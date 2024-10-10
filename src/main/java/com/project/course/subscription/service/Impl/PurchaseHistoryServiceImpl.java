package com.project.course.subscription.service.Impl;

import com.project.course.subscription.model.PurchaseHistory;
import com.project.course.subscription.repository.PurchaseHistoryRepository;
import com.project.course.subscription.service.PurchaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PurchaseHistoryServiceImpl implements PurchaseHistoryService {

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @Override
    public List<PurchaseHistory> getAllPurchaseSubscriptionHistory() {
        return purchaseHistoryRepository.findAll();
    }

    @Override
    public void createPurchaseHistory(PurchaseHistory purchaseHistory) {
        purchaseHistoryRepository.save(purchaseHistory);
    }

    @Override
    public List<PurchaseHistory> findPurchaseHistoryByUserAndSubscription(Long userId, Long subscriptionId) {
        return purchaseHistoryRepository.findPurchaseHistoryByUserAndSubscription(userId, subscriptionId);
    }

}
