package com.project.course.subscription.service;

import com.project.course.subscription.dto.PurchaseHistoryDTO;
import com.project.course.subscription.model.PurchaseHistory;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseHistoryService {

    List<PurchaseHistoryDTO> getAllPurchaseSubscriptionHistory();

    void createPurchaseHistory(PurchaseHistory purchaseHistory);

    List<PurchaseHistory> findPurchaseHistoryByUserAndSubscription(Long userId, Long subscriptionId);

    Page<PurchaseHistoryDTO> getPurchaseHistoriesByPaxUserUuid(String uuid, int page, int size, String sortBy, String direction, LocalDateTime purchaseDate, LocalDateTime expiryDate);
}
