package com.project.course.subscription.service.Impl;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.dto.PurchaseSubscriptionResponseDTO;
import com.project.course.subscription.email.EmailService;
import com.project.course.subscription.model.PaxUser;
import com.project.course.subscription.model.PurchaseHistory;
import com.project.course.subscription.model.PurchaseSubscription;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.repository.PurchaseSubscriptionRepository;
import com.project.course.subscription.service.PaxUserService;
import com.project.course.subscription.service.PaymentService;
import com.project.course.subscription.service.PurchaseHistoryService;
import com.project.course.subscription.service.PurchaseSubscriptionService;
import com.project.course.subscription.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.checkout.Session;

@Service
public class PurchaseSubscriptionServiceImpl implements PurchaseSubscriptionService {

	@Autowired
	private PurchaseSubscriptionRepository purchaseSubscriptionRepository;

	@Autowired
	private PaxUserService paxUserService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private PurchaseHistoryService purchaseHistoryService;

	@Autowired
	private EmailService emailservice;

	@Override
	public PurchaseSubscriptionDTO createPurchaseSubscription(PurchaseSubscriptionDTO purchaseSubscriptionDTO) {
		PaxUser paxUser = paxUserService.getHeadUserByUuid(purchaseSubscriptionDTO.getPaxUserUuid());
		Subscription subscription = subscriptionService
				.getSubscriptionByUuid(purchaseSubscriptionDTO.getSubscriptionUuid());
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
			throw new IllegalArgumentException(
					"PaxUser cannot purchase the same subscription until the previous one expires.");
		}
		PurchaseSubscription savedSubscription = purchaseSubscriptionRepository.save(purchaseSubscription);

		// Send purchase confirmation email
		String file = "SubscriptionConfirmation.html";
		if (purchaseSubscriptionDTO.getPaid()) {
			Session session = paymentService.createCheckoutSession(purchaseSubscriptionDTO);
			purchaseSubscription.setReturnUrl(session.getUrl());
			System.out.println("checkOutUrl :" + session.getUrl());
			purchaseSubscription.setPaymentId(session.getId());
		} else {
			emailservice.sendPurchaseConfirmEmail(paxUser.getEmail(), paxUser.getName(), subscription.getPlanName(),
					now.toLocalDate(), purchaseSubscription.getExpiryDate().toLocalDate(),
					subscription.getSubscriptionType(), "Pending", file);
		}
		// Create purchase history entry
		createPurchaseHistory(paxUser, subscription, now, purchaseSubscription.getExpiryDate(), 0, purchaseSubscription,
				now, now);

		return convertToDTO(savedSubscription);
	}

	@Override
	public PurchaseSubscriptionDTO paySubscriptions(String uuid, PurchaseSubscriptionDTO purchaseSubscriptionDTO) {
		Optional<PurchaseSubscription> subscriptionOpt = purchaseSubscriptionRepository.findByUuid(uuid);

		if (subscriptionOpt.isPresent()) {
			PurchaseSubscription subscription = subscriptionOpt.get();
			subscription.setCost(subscription.getCost()); // No need to call subscriptionOpt.get() again
			//subscription.setCurrency(purchaseSubscriptionDTO.getCurrency().toString());
			//subscription.setPaymentMethodType(purchaseSubscriptionDTO.getPaymentMethodType());
			//subscription.setPaymentMethodType(purchaseSubscriptionDTO.getPaymentMethodType().toString());
			//subscription.setSessionMode(purchaseSubscriptionDTO.getSessionMode().toString());
			
			Session session = paymentService.createCheckoutSession(purchaseSubscriptionDTO);
			
			subscription.setPaid(false);

			subscription.setReturnUrl(session.getUrl());
			System.out.println("checkOutUrl :" + session.getUrl());
			subscription.setSessionMode(session.getMode());
			subscription.setPaymentStatus(session.getPaymentStatus());
			subscription.setCurrency(session.getCurrency());
			subscription.setPaymentMethodType(purchaseSubscriptionDTO.getPaymentMethodType().toString());
			subscription.setTransactionId(session.getPaymentIntent());
			subscription.setPaymentId(session.getId());

			// Save updated subscription
			purchaseSubscriptionRepository.save(subscription);

			// Create Purchase History
			LocalDateTime purchaseDate = subscription.getPurchaseDate();
			LocalDateTime now = LocalDateTime.now();
			createPurchaseHistory(subscription.getPaxUser(), subscription.getSubscription(), purchaseDate,
					subscription.getExpiryDate(),
					getRenewalCount(subscription.getPaxUser(), subscription.getSubscription()), subscription, now, // paidDate
																													// is
																													// current																				// time
					null // cancelRecurringDate is null
			);

			// Send payment confirmation email
			String file1 = "PaymentConfirmation.html";
			emailservice.sendPaymentConfirmEmail(subscription.getPaxUser().getEmail(),
					subscription.getPaxUser().getName(), subscription.getPlanName(), purchaseDate.toLocalDate(),
					subscription.getExpiryDate().toLocalDate(), subscription.getSubscription().getSubscriptionType(),
					"Success", now.toLocalDate(), file1);

			// Return the DTO
			return convertToDTO(subscription);
		} else {
			throw new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid);
		}
	}

	
//	@Override
//	public String paySubscriptions(String uuid, PurchaseSubscriptionDTO purchaseSubscriptionDTO) {
//	    // Find the subscription by UUID
//	    Optional<PurchaseSubscription> subscriptionOpt = purchaseSubscriptionRepository.findByUuid(uuid);
//
//	    if (subscriptionOpt.isPresent()) {
//	        PurchaseSubscription purchaseSubscription = subscriptionOpt.get();
//
//	        // Create Stripe Checkout Session
//	        Session session = null;
//	        try {
//	            session = paymentService.createCheckoutSession(purchaseSubscriptionDTO);
//
//	            if (session == null) {
//	                throw new RuntimeException("Failed to create Stripe Checkout Session.");
//	            }
//	        } catch (Exception e) {
//	            // Log and throw meaningful exception
//	            //logger.error("Error during session creation for UUID: {}", uuid, e);
//	            throw new RuntimeException("Unable to process subscription payment. Please try again later.");
//	        }
//
//	        // Update subscription with session details
//	        purchaseSubscription.setPaid(false); // Assume not paid until confirmed
//	        purchaseSubscription.setPaymentMethodType(purchaseSubscriptionDTO.getPaymentMethodType() != null 
//	            ? purchaseSubscriptionDTO.getPaymentMethodType().toString() : "UNKNOWN");
//	        purchaseSubscription.setSessionMode(purchaseSubscriptionDTO.getSessionMode() != null 
//	            ? purchaseSubscriptionDTO.getSessionMode().toString() : "UNKNOWN");
//	        purchaseSubscription.setCurrency(purchaseSubscriptionDTO.getCurrency() != null 
//	            ? purchaseSubscriptionDTO.getCurrency().toString() : "USD");
//	        purchaseSubscription.setPaymentStatus("PENDING");
//
//	        // Save updated subscription
//	        purchaseSubscriptionRepository.save(purchaseSubscription);
//
//	        return session.getUrl(); // Return the checkout URL
//	    } else {
//	        // Log and throw custom exception
//	        //logger.warn("Subscription not found for UUID: {}", uuid);
//	        throw new RuntimeException("Subscription not found for UUID: " + uuid);
//	    }
//	}
	
	@Override
	public boolean disableRecurringForSubscription(String uuid) {
		Optional<PurchaseSubscription> subscriptionOpt = purchaseSubscriptionRepository.findByUuid(uuid);

		if (subscriptionOpt.isPresent()) {
			PurchaseSubscription subscription = subscriptionOpt.get();
			PurchaseSubscription purchaseSubscription = new PurchaseSubscription();
			purchaseSubscription.setPaymentMethodType(uuid);
			// Set recurring to false and save
			subscription.setRecurring(false);
			purchaseSubscriptionRepository.save(subscription);
			// Create a new PurchaseHistory entry to track this payment
			LocalDateTime purchaseDate = subscription.getPurchaseDate();
			LocalDateTime now = LocalDateTime.now();
			createPurchaseHistory(subscription.getPaxUser(), subscription.getSubscription(), purchaseDate,
					subscription.getExpiryDate(),
					getRenewalCount(subscription.getPaxUser(), subscription.getSubscription()), subscription, null, // paidDate
																													// remains																// null
					now // cancelRecurringDate is the current time
			);
			String file = "RenewalCancellation.html";
			emailservice.sendAutoRenewalCancellationEmail(subscription.getPaxUser().getEmail(),
					subscription.getPaxUser().getName(), subscription.getSubscription().getPlanName(),
					subscription.getExpiryDate().toLocalDate(), file);
			return true;
		} else {
			throw new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid);
		}
	}

	@Scheduled(cron = "0 0 12 * * ?")
	// @Scheduled(cron = "0 */2 * * * ?") // Runs every 2 minutes
	public void renewExpiredRecurringSubscriptions() {
		// Fetch expired recurring subscriptions
		List<PurchaseSubscription> expiredSubscriptions = purchaseSubscriptionRepository
				.findExpiredRecurringSubscriptions(LocalDateTime.now());

		for (PurchaseSubscription subscription : expiredSubscriptions) {

			Subscription plan = subscription.getSubscription();

			// Check if the plan exists and is not deleted/inactive
			if (plan == null || !plan.isActive()) {
				continue; // Skip this subscription
			}

			// Check if the subscription is recurring and paid
			boolean isRecurring = subscription.isRecurring();
			Boolean getPaid = subscription.getPaid();

			// If the subscription is recurring and paid is true, proceed with renewal
			if (isRecurring && Boolean.TRUE.equals(getPaid)) {
				// Calculate the new expiry date based on the subscription type
				LocalDateTime newExpiryDate = calculateExpiryDate(subscription.getExpiryDate(),
						Subscription.SubscriptionType.valueOf(subscription.getSubscriptionType()));

				// Update the expiry date to the current time (hours, minutes, seconds) during
				// renewal
				newExpiryDate = newExpiryDate.withHour(LocalDateTime.now().getHour())
						.withMinute(LocalDateTime.now().getMinute()).withSecond(LocalDateTime.now().getSecond());

				// Update the expiry date of the existing PurchaseSubscription
				subscription.setExpiryDate(newExpiryDate);
				purchaseSubscriptionRepository.save(subscription);

				// Send renewal email
				String file = "RenewalAlert.html";
				emailservice.sendRenewalEmail(subscription.getPaxUser().getEmail(), subscription.getPaxUser().getName(),
						subscription.getSubscription().getPlanName(), LocalDate.now(), newExpiryDate.toLocalDate(),
						Subscription.SubscriptionType.valueOf(subscription.getSubscriptionType()), file);

				// Optionally, you can still create a new PurchaseHistory entry for tracking
				// purposes
				int newRenewalCount = getRenewalCount(subscription.getPaxUser(), subscription.getSubscription());
				createPurchaseHistory(subscription.getPaxUser(), subscription.getSubscription(), LocalDateTime.now(),
						newExpiryDate, newRenewalCount, subscription, LocalDateTime.now(), null);
			}
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

	@Override
	public List<PurchaseSubscription> getAllPaxHeadIdBySubscriptionId(Long id) {
		return purchaseSubscriptionRepository.findAllPaxUserIdsBySubscriptionIdAndRecurring(id, true);
	}

	private void createPurchaseHistory(PaxUser paxUser, Subscription subscription, LocalDateTime purchaseDate,
			LocalDateTime expiryDate, int renewalCount, PurchaseSubscription purchaseSubscription,
			LocalDateTime paidDate, LocalDateTime cancelRecurringDate) {
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
		purchaseHistory.setCancelRecurringDate(!purchaseSubscription.isRecurring() ? cancelRecurringDate : null);
		purchaseHistory.setSubscriptionUuid(purchaseSubscription.getSubscription().getUuid());

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
		return Optional
				.ofNullable(purchaseSubscriptionRepository.findByUuid(uuid).filter(PurchaseSubscription::isActive)
						.map(this::convertToDTO).orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
								"Purchase Subscription not found with UUID: " + uuid)));
	}

	@Override
	public List<PurchaseSubscriptionResponseDTO> getActiveSubscriptionsByPaxUserUuid(String paxUserUuid) {
		List<PurchaseSubscription> subscriptions = purchaseSubscriptionRepository
				.findActiveSubscriptionsByPaxUserUuid(paxUserUuid, LocalDateTime.now());
		return subscriptions.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
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
		dto.setCheckOutUrl(purchaseSubscription.getReturnUrl());
		dto.setPlanName(purchaseSubscription.getPlanName());
		dto.setCost(purchaseSubscription.getCost());
		dto.setSubscriptionType(purchaseSubscription.getSubscriptionType());
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

	@Override
	public void updatepurchaseSubscriptionService(Session session) throws StripeException {
		Optional<PurchaseSubscription> paymentId = purchaseSubscriptionRepository.findByPaymentId(session.getId());
		if (paymentId.isPresent()) {
			PurchaseSubscription subscription = paymentId.get();
			subscription.setPaymentStatus(session.getPaymentStatus());

			String paymentIntentId = session.getPaymentIntent();
			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
			String paymentMethodId = paymentIntent.getPaymentMethod();
			PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

			subscription.setPaymentMethodType(paymentIntent.getPaymentMethodTypes().get(0));
			
			subscription.setSessionMode(session.getMode());
			subscription.setCurrency(session.getCurrency());
			subscription.setPaid(true);
			purchaseSubscriptionRepository.save(subscription);
			
			PurchaseSubscription purchaseSubscription=paymentId.get();
			LocalDateTime now = LocalDateTime.now();
			String template = "SubscriptionConfirmation.html";
			emailservice.sendPaymentConfirmEmail(
					purchaseSubscription.getPaxUser().getEmail(),
					purchaseSubscription.getPaxUser().getName(),
					purchaseSubscription.getSubscription().getPlanName(),
					now.toLocalDate(),
					purchaseSubscription.getExpiryDate().toLocalDate(), 
					purchaseSubscription.getSubscription().getSubscriptionType(),
					"Sucess", now.toLocalDate(), template);
//          purchaseSubscription.setSessionMode(session.getMode());
//          purchaseSubscription.setPaymentStatus(session.getPaymentStatus());
//          purchaseSubscription.setCurrency(session.getCurrency());
//          purchaseSubscription.setPaymentMethodType(purchaseSubscriptionDTO.getPaymentMethodType().toString());
//          purchaseSubscription.setTransactionId(session.getPaymentIntent());


		}
	}

}