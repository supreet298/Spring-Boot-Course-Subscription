package com.project.course.subscription.service.Impl;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.project.course.subscription.dto.PurchaseSubscriptionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.email.EmailService;
import com.project.course.subscription.model.PaxUser;
import com.project.course.subscription.model.PurchaseHistory;
import com.project.course.subscription.model.PurchaseSubscription;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.repository.PurchaseSubscriptionRepository;
import com.project.course.subscription.service.PaxUserService;
import com.project.course.subscription.service.PurchaseHistoryService;
import com.project.course.subscription.service.PurchaseSubscriptionService;
import com.project.course.subscription.service.SubscriptionService;

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

    @Autowired
    private EmailService emailservice;

    @Override
    public PurchaseSubscriptionDTO createPurchaseSubscription(PurchaseSubscriptionDTO purchaseSubscriptionDTO) {

        // Fetch PaxUser by ID
        PaxUser paxUser = paxUserService.getHeadUserByUuid(purchaseSubscriptionDTO.getPaxUserUuid());
        // Fetch Subscription by ID
        Subscription subscription = subscriptionService.getSubscriptionByUuid(purchaseSubscriptionDTO.getSubscriptionUuid());

        // Initialize PurchaseSubscription from DTO
        PurchaseSubscription purchaseSubscription = new PurchaseSubscription();
        purchaseSubscription.setPaxUser(paxUser);
        purchaseSubscription.setSubscription(subscription);
        purchaseSubscription.setNotificationType(purchaseSubscriptionDTO.getNotificationType());
        purchaseSubscription.setPaid(purchaseSubscriptionDTO.getPaid());
        purchaseSubscription.setRecurring(purchaseSubscriptionDTO.isRecurring());

        LocalDateTime now = LocalDateTime.now();
        purchaseSubscription.setPurchaseDate(now);
        purchaseSubscription.setExpiryDate(calculateExpiryDate(now, subscription.getSubscriptionType()));

        purchaseSubscription.setPlanName(subscription.getPlanName());
        purchaseSubscription.setCost(subscription.getCost());
        purchaseSubscription.setSubscriptionType(subscription.getSubscriptionType().toString());

        // Check if the user has an active subscription for the same plan
        if (hasActiveSubscriptionForSamePlan(paxUser, subscription)) {
            throw new IllegalArgumentException("PaxUser cannot purchase the same subscription until the previous one expires.");
        }

        // Save PurchaseSubscription
        PurchaseSubscription savedSubscription = purchaseSubscriptionRepository.save(purchaseSubscription);

        // Send confirmation email
        if(purchaseSubscriptionDTO.getPaid())
        {
        String file = "SubscriptionConfirmation.html";
        emailservice.sendPurchaseConfirmEmail(paxUser.getEmail(), paxUser.getName(), subscription.getPlanName(), now.toLocalDate(), purchaseSubscription.getExpiryDate().toLocalDate(), subscription.getSubscriptionType(),"Success", file);
        }else
        {
        	String file = "SubscriptionConfirmation.html";
            emailservice.sendPurchaseConfirmEmail(paxUser.getEmail(), paxUser.getName(), subscription.getPlanName(), now.toLocalDate(), purchaseSubscription.getExpiryDate().toLocalDate(), subscription.getSubscriptionType(),"Pending", file);
        }
        // Create purchase history entry
        createPurchaseHistory(paxUser, subscription, now, purchaseSubscription.getExpiryDate(), 0, purchaseSubscription,now);

        return convertToDTO(savedSubscription);

    }

    @Scheduled(cron = "0 0 12 * * ?")
    //@Scheduled(cron = "0 */2 * * * ?") // Runs every 2 minutes

    public void renewExpiredRecurringSubscriptions() {
        // Fetch expired recurring subscriptions
        List<PurchaseSubscription> expiredSubscriptions = purchaseSubscriptionRepository.findExpiredRecurringSubscriptions(LocalDateTime.now());

        for (PurchaseSubscription subscription : expiredSubscriptions) {
            // Check if the subscription is recurring and paid
            boolean isRecurring = subscription.isRecurring();
            Boolean getPaid = subscription.getPaid();

            // If the subscription is recurring and paid is true, proceed with renewal
            if (isRecurring && Boolean.TRUE.equals(getPaid)) {
                // Calculate the new expiry date based on the subscription type
                LocalDateTime newExpiryDate = calculateExpiryDate(subscription.getExpiryDate(),Subscription.SubscriptionType.valueOf(subscription.getSubscriptionType()));

                // Update the expiry date to the current time (hours, minutes, seconds) during renewal
                newExpiryDate = newExpiryDate.withHour(LocalDateTime.now().getHour()).withMinute(LocalDateTime.now().getMinute()).withSecond(LocalDateTime.now().getSecond());

                // Update the expiry date of the existing PurchaseSubscription
                subscription.setExpiryDate(newExpiryDate);
                purchaseSubscriptionRepository.save(subscription);

                // Send renewal email
                String file = "RenewalAlert.html";
                emailservice.sendRenewalEmail(subscription.getPaxUser().getEmail(), subscription.getPaxUser().getName(), subscription.getSubscription().getPlanName(), LocalDate.now(), newExpiryDate.toLocalDate(), Subscription.SubscriptionType.valueOf(subscription.getSubscriptionType()), file);

                // Optionally, you can still create a new PurchaseHistory entry for tracking purposes
                int newRenewalCount = getRenewalCount(subscription.getPaxUser(), subscription.getSubscription());
                createPurchaseHistory(subscription.getPaxUser(), subscription.getSubscription(), LocalDateTime.now(), newExpiryDate, newRenewalCount, subscription,LocalDateTime.now());
            }
        }
    }


    private boolean hasActiveSubscriptionForSamePlan(PaxUser paxUser, Subscription subscription) {
        List<PurchaseSubscription> activeSubscriptions = purchaseSubscriptionRepository.findActiveSubscriptionsByUserId(paxUser.getId());

        // Check if any active subscription matches the subscription being purchased
        return activeSubscriptions.stream().anyMatch(ps -> ps.getSubscription().getId().equals(subscription.getId()));
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

    private void createPurchaseHistory(PaxUser paxUser, Subscription subscription, LocalDateTime purchaseDate, LocalDateTime expiryDate, int renewalCount, PurchaseSubscription purchaseSubscription,LocalDateTime paidDate) {
        PurchaseHistory purchaseHistory = new PurchaseHistory();
        purchaseHistory.setPaxUser(paxUser);
        purchaseHistory.setSubscription(subscription);
        purchaseHistory.setClientName(paxUser.getName());
        purchaseHistory.setClientEmail(paxUser.getEmail());
        purchaseHistory.setPlanName(purchaseSubscription.getPlanName());
        purchaseHistory.setSubscriptionType(purchaseSubscription.getSubscriptionType());
        purchaseHistory.setRenewalCount(renewalCount);
        purchaseHistory.setPurchaseSubscriptionUuid(purchaseSubscription.getUuid());
        purchaseHistory.setPurchaseDate(purchaseDate);
        purchaseHistory.setExpiryDate(expiryDate);
        purchaseHistory.setNotificationType(purchaseSubscription.getNotificationType().toString());
        purchaseHistory.setCost(purchaseSubscription.getCost());
        purchaseHistory.setPaid(purchaseSubscription.getPaid());
        purchaseHistory.setPaidDate(purchaseSubscription.getPaid() ? paidDate : null);
        purchaseHistory.setRecurring(purchaseSubscription.isRecurring());

        purchaseHistoryService.createPurchaseHistory(purchaseHistory); // Save purchase history
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
    public boolean disableRecurringForSubscription(String uuid) {
        Optional<PurchaseSubscription> subscriptionOpt = purchaseSubscriptionRepository.findByUuid(uuid);

        if (subscriptionOpt.isPresent()) {
            PurchaseSubscription subscription = subscriptionOpt.get();

            // Set recurring to false and save
            subscription.setRecurring(false);
            purchaseSubscriptionRepository.save(subscription);
            // Create a new PurchaseHistory entry to track this payment
            LocalDateTime purchaseDate = subscription.getPurchaseDate();
            LocalDateTime now = LocalDateTime.now();
            createPurchaseHistory(
                    subscription.getPaxUser(),
                    subscription.getSubscription(),
                    purchaseDate,
                    subscription.getExpiryDate(),
                    getRenewalCount(subscription.getPaxUser(), subscription.getSubscription()),
                    subscription,
                    now
            );
            String file = "RenewalCancellation.html";
            emailservice.sendAutoRenewalCancellationEmail(subscription.getPaxUser().getEmail(), subscription.getPaxUser().getName(), subscription.getSubscription().getPlanName(), subscription.getExpiryDate().toLocalDate(), file);
            return true;
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid);
        }
    }

    @Override
    public List<PurchaseSubscriptionResponseDTO> getActiveSubscriptionsByPaxUserUuid(String paxUserUuid) {
        List<PurchaseSubscription> subscriptions = purchaseSubscriptionRepository.findActiveSubscriptionsByPaxUserUuid(paxUserUuid, LocalDateTime.now());
        return subscriptions.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public boolean paySubscription(String uuid) {
        Optional<PurchaseSubscription> subscriptionOpt = purchaseSubscriptionRepository.findByUuid(uuid);

        if (subscriptionOpt.isPresent()) {
            PurchaseSubscription subscription = subscriptionOpt.get();

            // Set paid to true and save
            subscription.setPaid(true);
            purchaseSubscriptionRepository.save(subscription);
            
            // Create a new PurchaseHistory entry to track this payment
            LocalDateTime purchaseDate = subscription.getPurchaseDate();
            LocalDateTime now = LocalDateTime.now();
            createPurchaseHistory(
                    subscription.getPaxUser(),
                    subscription.getSubscription(),
                    purchaseDate,
                    subscription.getExpiryDate(),
                    getRenewalCount(subscription.getPaxUser(), subscription.getSubscription()),
                    subscription,
                    now
            );
            String file1="PaymentConfirmation.html";
            emailservice.sendpaymentConfirmEmail(subscription.getPaxUser().getEmail(), subscription.getPaxUser().getName(), subscription.getPlanName(), purchaseDate.toLocalDate(), subscription.getExpiryDate().toLocalDate(),  subscription.getSubscription().getSubscriptionType(), "Sucess", now.toLocalDate(), file1);
            return true;
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid);
        }
    }

    private PurchaseSubscriptionDTO convertToDTO(PurchaseSubscription purchaseSubscription) {
        PurchaseSubscriptionDTO dto = new PurchaseSubscriptionDTO();
        dto.setPaxUserUuid(purchaseSubscription.getPaxUser().getUuid());
        dto.setSubscriptionUuid(purchaseSubscription.getSubscription().getUuid());
        dto.setRecurring(purchaseSubscription.isRecurring());
        dto.setNotificationType(purchaseSubscription.getNotificationType());
        dto.setPaid(purchaseSubscription.getPaid());
        dto.setPurchaseDate(purchaseSubscription.getPurchaseDate());
        dto.setExpiryDate(purchaseSubscription.getExpiryDate());
        return dto;
    }

    private PurchaseSubscriptionResponseDTO convertToResponseDTO(PurchaseSubscription purchaseSubscription) {
        PurchaseSubscriptionResponseDTO dto = new PurchaseSubscriptionResponseDTO();
        dto.setUuid(purchaseSubscription.getUuid());
        dto.setPaxUserUuid(purchaseSubscription.getPaxUser().getUuid());
        dto.setSubscriptionName(purchaseSubscription.getPlanName());
        dto.setSubscriptionType(purchaseSubscription.getSubscriptionType());
        dto.setRecurring(purchaseSubscription.isRecurring());
        dto.setPaid(purchaseSubscription.getPaid());
        dto.setPurchaseDate(purchaseSubscription.getPurchaseDate());
        dto.setExpiryDate(purchaseSubscription.getExpiryDate());
        return dto;
    }
}