package com.project.course.subscription.service.Impl;

import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.model.PaxUser;
import com.project.course.subscription.model.PurchaseSubscription;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.repository.PurchaseSubscriptionRepository;
import com.project.course.subscription.service.PaxUserService;
import com.project.course.subscription.service.PurchaseSubscriptionService;
import com.project.course.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseSubscriptionServiceImpl implements PurchaseSubscriptionService {

    @Autowired
    private PurchaseSubscriptionRepository purchaseSubscriptionRepository;

    @Autowired
    private PaxUserService paxUserService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Override
    public PurchaseSubscription createPurchaseSubscription(PurchaseSubscription purchaseSubscription) {
        // Fetch the PaxUser and check if it's of type HEAD in one step
        PaxUser paxUser = paxUserService.getHeadUserById(purchaseSubscription.getPaxUser().getId());
        purchaseSubscription.setPaxUser(paxUser);

        // Fetch the Subscription using the SubscriptionService
        Subscription subscription = subscriptionService.getSubscriptionById(purchaseSubscription.getSubscription().getId());
        purchaseSubscription.setSubscription(subscription);

        // Automatically set purchaseDate
        LocalDateTime now = LocalDateTime.now();
        purchaseSubscription.setPurchaseDate(now);

        // Set expiryDate based on SubscriptionType directly from subscription service
        LocalDateTime expiryDate = calculateExpiryDate(now, subscription.getSubscriptionType());
        purchaseSubscription.setExpiryDate(expiryDate);
        return purchaseSubscriptionRepository.save(purchaseSubscription);
    }

    @Override
    public List<PurchaseSubscriptionDTO> getAllPurchaseSubscriptions() {
        List<PurchaseSubscription> purchaseSubscriptions = purchaseSubscriptionRepository.findAll();
        return purchaseSubscriptions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PurchaseSubscriptionDTO convertToDTO(PurchaseSubscription purchaseSubscription) {
        PurchaseSubscriptionDTO dto = new PurchaseSubscriptionDTO();
        dto.setUuid(purchaseSubscription.getUuid());
        dto.setPlanName(purchaseSubscription.getSubscription().getPlanName());
        dto.setPaxUserHead(purchaseSubscription.getPaxUser().getUserName());
        dto.setRecurring(purchaseSubscription.isRecurring());
        dto.setPaid(purchaseSubscription.isPaid());
        dto.setPurchaseDate(purchaseSubscription.getPurchaseDate());
        dto.setExpiryDate(purchaseSubscription.getExpiryDate());
        return dto;
    }

    private LocalDateTime calculateExpiryDate(LocalDateTime startDate, Subscription.SubscriptionType subscriptionType) {
        return switch (subscriptionType) {
            case MONTHLY -> startDate.plusMonths(1);
            case QUARTERLY -> startDate.plusMonths(3);
            case HALF_YEARLY -> startDate.plusMonths(6);
            case YEARLY -> startDate.plusYears(1);
            default -> throw new IllegalArgumentException("Unknown SubscriptionType: " + subscriptionType);
        };
    }
}
