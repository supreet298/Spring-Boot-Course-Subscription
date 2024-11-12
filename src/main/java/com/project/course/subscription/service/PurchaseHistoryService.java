package com.project.course.subscription.service;

import com.project.course.subscription.dto.PurchaseHistoryDTO;
import com.project.course.subscription.model.PurchaseHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PurchaseHistoryService {

    List<PurchaseHistoryDTO> getAllPurchaseSubscriptionHistory();

    void createPurchaseHistory(PurchaseHistory purchaseHistory);

    List<PurchaseHistory> findPurchaseHistoryByUserAndSubscription(Long userId, Long subscriptionId);

    Page<PurchaseHistoryDTO> getPurchaseHistoriesByPaxUserUuid(String uuid,Pageable pageable);

}
