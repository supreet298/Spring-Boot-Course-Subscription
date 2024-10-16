package com.project.course.subscription.service.Impl;

import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.model.PaxUser;
import com.project.course.subscription.model.PurchaseHistory;
import com.project.course.subscription.model.PurchaseSubscription;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.repository.PurchaseSubscriptionRepository;
import com.project.course.subscription.service.PaxUserService;
import com.project.course.subscription.service.PurchaseHistoryService;
import com.project.course.subscription.service.PurchaseSubscriptionService;
import com.project.course.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PurchaseSubscriptionServiceImpl implements PurchaseSubscriptionService {

    @Autowired
    private PurchaseSubscriptionRepository purchaseSubscriptionRepository;

    @Autowired
    private PaxUserService paxUserService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private PurchaseHistoryService purchaseHistoryService;

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

        // Validate if the purchase should proceed
        if (purchaseSubscription.getPaid() == null || !purchaseSubscription.getPaid()){
            throw new IllegalArgumentException("The subscription cannot be created as the payment has not been made. ");
        }

        // Check if the user has an active subscription for the same plan
        if (hasActiveSubscriptionForSamePlan(paxUser, subscription)) {
            throw new IllegalArgumentException("PaxUser cannot purchase the same subscription until the previous one expires.");
        }
        // Save the PurchaseSubscription
        PurchaseSubscription savedSubscription = purchaseSubscriptionRepository.save(purchaseSubscription);

        // Create PurchaseHistory with initial details
        createPurchaseHistory(paxUser, subscription, now, expiryDate,0);

        return savedSubscription;
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void renewExpiredRecurringSubscriptions() {
        // Fetch expired recurring subscriptions
        List<PurchaseSubscription> expiredSubscriptions = purchaseSubscriptionRepository.findExpiredRecurringSubscriptions(LocalDateTime.now());

        for (PurchaseSubscription subscription : expiredSubscriptions) {
            // Check if the subscription is recurring and paid
            boolean isRecurring = subscription.isRecurring();
            Boolean getPaid = subscription.getPaid();

            // If the subscription is recurring and paid is true, proceed with renewal
            if (isRecurring && Boolean.TRUE.equals(getPaid)) {
                // Increment the renewal count
                int newRenewalCount = getRenewalCount(subscription.getPaxUser(), subscription.getSubscription());

                // Calculate new expiry date based on the subscription type
                LocalDateTime newExpiryDate = calculateExpiryDate(subscription.getExpiryDate(), subscription.getSubscription().getSubscriptionType());

                // Create a new PurchaseHistory entry for the renewal
                createPurchaseHistory(subscription.getPaxUser(), subscription.getSubscription(), LocalDateTime.now(), newExpiryDate, newRenewalCount);
            }
            // Else, skip the renewal if either condition is not met
        }
    }

    private boolean hasActiveSubscriptionForSamePlan(PaxUser paxUser, Subscription subscription) {
        List<PurchaseSubscription> activeSubscriptions = purchaseSubscriptionRepository.findActiveSubscriptionsByUserId(paxUser.getId());

        // Check if any active subscription matches the subscription being purchased
        return activeSubscriptions.stream()
                .anyMatch(ps -> ps.getSubscription().getId().equals(subscription.getId()));
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

    private void createPurchaseHistory(PaxUser paxUser, Subscription subscription, LocalDateTime purchaseDate, LocalDateTime expiryDate,int renewalCount) {
        PurchaseHistory purchaseHistory = new PurchaseHistory();
        purchaseHistory.setPaxUserHead(paxUser);
        purchaseHistory.setSubscription(subscription);
        purchaseHistory.setPlanName(subscription.getPlanName());
        purchaseHistory.setRenewalCount(renewalCount);
        purchaseHistory.setPurchaseDate(purchaseDate);
        purchaseHistory.setExpiryDate(expiryDate);

        purchaseHistoryService.createPurchaseHistory(purchaseHistory);  // Save purchase history
    }

    private int getRenewalCount(PaxUser paxUser, Subscription subscription) {
        List<PurchaseHistory> historyList = purchaseHistoryService.findPurchaseHistoryByUserAndSubscription(paxUser.getId(), subscription.getId());
        return historyList.size();
    }

    @Override
    public List<PurchaseSubscriptionDTO> getAllPurchaseSubscriptions() {
        List<PurchaseSubscription> purchaseSubscriptions = purchaseSubscriptionRepository.findAll();
        return purchaseSubscriptions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PurchaseSubscriptionDTO> getPurchaseSubscriptionByUuid(String uuid) {
        return Optional.ofNullable(purchaseSubscriptionRepository.findByUuid(uuid)
                .filter(PurchaseSubscription::isActive)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Purchase Subscription not found with UUID: " + uuid)));
    }

    @Override
    public boolean deletePurchaseSubscription(String uuid) {
        Optional<PurchaseSubscription> existingPurchaseSubscription = purchaseSubscriptionRepository.findByUuid(uuid);
        if (existingPurchaseSubscription.isPresent()) {
            PurchaseSubscription purchaseSubscription = existingPurchaseSubscription.get();
            purchaseSubscription.setActive(false);
            purchaseSubscriptionRepository.save(purchaseSubscription);
            return true;
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Purchase Subscription not found with UUID: " + uuid);
        }
    }

    private PurchaseSubscriptionDTO convertToDTO(PurchaseSubscription purchaseSubscription) {
        PurchaseSubscriptionDTO dto = new PurchaseSubscriptionDTO();
        dto.setUuid(purchaseSubscription.getUuid());
        dto.setPlanName(purchaseSubscription.getSubscription().getPlanName());
        dto.setPaxUserHead(purchaseSubscription.getPaxUser().getUserName());
        dto.setRecurring(purchaseSubscription.isRecurring());
        dto.setPaid(purchaseSubscription.getPaid());
        dto.setPurchaseDate(purchaseSubscription.getPurchaseDate());
        dto.setExpiryDate(purchaseSubscription.getExpiryDate());
        return dto;
    }

}
