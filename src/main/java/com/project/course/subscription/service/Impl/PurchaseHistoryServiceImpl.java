package com.project.course.subscription.service.Impl;

import com.project.course.subscription.dto.PurchaseHistoryDTO;
import com.project.course.subscription.model.PurchaseHistory;
import com.project.course.subscription.repository.PurchaseHistoryRepository;
import com.project.course.subscription.service.PurchaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseHistoryServiceImpl implements PurchaseHistoryService {

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @Override
    public List<PurchaseHistoryDTO> getAllPurchaseSubscriptionHistory() {
        List<PurchaseHistory> histories = purchaseHistoryRepository.findAll();
        return histories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createPurchaseHistory(PurchaseHistory purchaseHistory) {
        purchaseHistoryRepository.save(purchaseHistory);
    }

    @Override
    public List<PurchaseHistory> findPurchaseHistoryByUserAndSubscription(Long userId, Long subscriptionId) {
        return purchaseHistoryRepository.findPurchaseHistoryByUserAndSubscription(userId, subscriptionId);
    }

    @Override
    public Page<PurchaseHistoryDTO> getPurchaseHistoriesByPaxUserUuid(
            String uuid, int page, int size, String sortBy, String direction,
            LocalDateTime purchaseDate, LocalDateTime expiryDate) {

        Sort.Order order = direction.equalsIgnoreCase("desc")
                ? Sort.Order.desc(sortBy)
                : Sort.Order.asc(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        // Call the repository method to fetch the filtered records
        return purchaseHistoryRepository.findByPaxUserUuidAndOptionalDates(
                        uuid,purchaseDate, expiryDate, pageable)
                .map(this::convertToDTO);
    }

    private PurchaseHistoryDTO convertToDTO(PurchaseHistory history) {
        PurchaseHistoryDTO dto = new PurchaseHistoryDTO();
        dto.setPaxUserUuid(history.getPaxUser().getUuid());
        dto.setPurchaseSubscriptionUuid(history.getPurchaseSubscriptionUuid());
        dto.setClientName(history.getClientName());
        dto.setClientEmail(history.getClientEmail());
        dto.setPlanName(history.getPlanName());
        dto.setSubscriptionType(history.getSubscriptionType());
        dto.setRenewalCount(history.getRenewalCount());
        dto.setPurchaseDate(history.getPurchaseDate());
        dto.setExpiryDate(history.getExpiryDate());
        dto.setNotificationType(history.getNotificationType());
        dto.setCost(history.getCost());
        dto.setPaid(history.getPaid());
        dto.setPaidDate(history.getPaidDate());
        dto.setRecurring(history.getRecurring());
        return dto;
    }
}
