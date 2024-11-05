package com.project.course.subscription.service.Impl;

import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.email.EmailService;
import com.project.course.subscription.model.PaxUser;
import com.project.course.subscription.model.PurchaseHistory;
import com.project.course.subscription.model.PurchaseSubscription;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.repository.PurchaseSubscriptionRepository;
import com.project.course.subscription.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

		// Validate if the purchase should proceed
		if (purchaseSubscription.getPaid() == null || !purchaseSubscription.getPaid()) {
			throw new IllegalArgumentException("The subscription cannot be created as the payment has not been made.");
		}

		// Check if the user has an active subscription for the same plan
		if (hasActiveSubscriptionForSamePlan(paxUser, subscription)) {
			throw new IllegalArgumentException("PaxUser cannot purchase the same subscription until the previous one expires.");
		}

		// Save PurchaseSubscription
		PurchaseSubscription savedSubscription = purchaseSubscriptionRepository.save(purchaseSubscription);

		// Send confirmation email
		String file = "SubscriptionConfirmation.html";
		emailservice.sendConfirmEmail(
				paxUser.getEmail(), paxUser.getName(),
				subscription.getPlanName(), now,
				purchaseSubscription.getExpiryDate(),
				subscription.getSubscriptionType(), file
		);

		// Create purchase history entry
		createPurchaseHistory(paxUser, subscription, now, purchaseSubscription.getExpiryDate(), 0, purchaseSubscription);

		return convertToDTO(savedSubscription);

	}

	@Scheduled(cron = "0 0 12 * * ?")
	public void renewExpiredRecurringSubscriptions() {
		// Fetch expired recurring subscriptions
		List<PurchaseSubscription> expiredSubscriptions = purchaseSubscriptionRepository
				.findExpiredRecurringSubscriptions(LocalDateTime.now());

		for (PurchaseSubscription subscription : expiredSubscriptions) {
			// Check if the subscription is recurring and paid
			boolean isRecurring = subscription.isRecurring();
			Boolean getPaid = subscription.getPaid();

			// If the subscription is recurring and paid is true, proceed with renewal
			if (isRecurring && Boolean.TRUE.equals(getPaid)) {
				// Increment the renewal count
				int newRenewalCount = getRenewalCount(subscription.getPaxUser(), subscription.getSubscription());

				// Calculate new expiry date based on the subscription type
				LocalDateTime newExpiryDate = calculateExpiryDate(subscription.getExpiryDate(),
						subscription.getSubscription().getSubscriptionType());

				// Create a new PurchaseHistory entry for the renewal
				createPurchaseHistory(subscription.getPaxUser(), subscription.getSubscription(), LocalDateTime.now(),
						newExpiryDate, newRenewalCount, subscription);
			}
			// Else, skip the renewal if either condition is not met
		}
	}

	private boolean hasActiveSubscriptionForSamePlan(PaxUser paxUser, Subscription subscription) {
		List<PurchaseSubscription> activeSubscriptions = purchaseSubscriptionRepository
				.findActiveSubscriptionsByUserId(paxUser.getId());

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

	private void createPurchaseHistory(PaxUser paxUser, Subscription subscription, LocalDateTime purchaseDate,
			LocalDateTime expiryDate, int renewalCount, PurchaseSubscription purchaseSubscription) {
		PurchaseHistory purchaseHistory = new PurchaseHistory();
		purchaseHistory.setPaxUser(paxUser);
		purchaseHistory.setSubscription(subscription);
		purchaseHistory.setClientName(paxUser.getName());
		purchaseHistory.setClientEmail(paxUser.getEmail());
		purchaseHistory.setPlanName(subscription.getPlanName());
		purchaseHistory.setRenewalCount(renewalCount);
		purchaseHistory.setPurchaseSubscriptionUuid(purchaseSubscription.getUuid());
		purchaseHistory.setPurchaseDate(purchaseDate);
		purchaseHistory.setExpiryDate(expiryDate);
		purchaseHistory.setNotificationType(purchaseSubscription.getNotificationType().toString());

		purchaseHistoryService.createPurchaseHistory(purchaseHistory); // Save purchase history
	}

	private int getRenewalCount(PaxUser paxUser, Subscription subscription) {
		List<PurchaseHistory> historyList = purchaseHistoryService
				.findPurchaseHistoryByUserAndSubscription(paxUser.getId(), subscription.getId());
		return historyList.size();
	}

	@Override
	public List<PurchaseSubscriptionDTO> getAllPurchaseSubscriptions() {
		List<PurchaseSubscription> purchaseSubscriptions = purchaseSubscriptionRepository.findAll();
		return purchaseSubscriptions.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Override
	public Optional<PurchaseSubscriptionDTO> getPurchaseSubscriptionByUuid(String uuid) {
		return Optional.ofNullable(purchaseSubscriptionRepository.findByUuid(uuid).filter(PurchaseSubscription::isActive)
				.map(this::convertToDTO).orElseThrow(() -> new ResponseStatusException(NOT_FOUND,"Purchase Subscription not found with UUID: " + uuid)));
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

	@Override
	public boolean disableRecurringForSubscription(String uuid) {
		Optional<PurchaseSubscription> subscriptionOpt = purchaseSubscriptionRepository.findByUuid(uuid);

		if (subscriptionOpt.isPresent()) {
			PurchaseSubscription subscription = subscriptionOpt.get();

			// Set recurring to false and save
			subscription.setRecurring(false);
			purchaseSubscriptionRepository.save(subscription);
			return true;
		} else {
			throw new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid);
		}
	}

	public PurchaseSubscription getPurchaseSubscriptionId(Long id) {
		return purchaseSubscriptionRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Subscription not found"));
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
}