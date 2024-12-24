package com.project.course.subscription.service.Impl;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.course.subscription.dto.SubscriptionDTO;
import com.project.course.subscription.email.EmailService;
import com.project.course.subscription.model.PurchaseSubscription;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.repository.SubscriptionRepository;
import com.project.course.subscription.service.PurchaseSubscriptionService;
import com.project.course.subscription.service.SubscriptionService;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	@Autowired
	private SubscriptionRepository subscriptionRepository;
	
	@Autowired
	@Lazy
	private PurchaseSubscriptionService purchaseSubscriptionService;
	
	@Autowired
	private EmailService emailservice;

	@Override
	public Subscription createSubscription(Subscription subscription) {
		Subscription sub = new Subscription();
		if (subscriptionRepository.existsByPlanNameAndIsActiveTrue(subscription.getPlanName())) {
			throw new RuntimeException("PlanName already exists, try another.");
		}
		sub.setPlanName(subscription.getPlanName());
		sub.setDescription(subscription.getDescription());
		if (subscription.getCost() == null || subscription.getCost() < 1) {
			throw new RuntimeException("Minimum Value Should be 1");
		}
		sub.setCost(subscription.getCost());
		sub.setSubscriptionType(subscription.getSubscriptionType());
		return subscriptionRepository.save(sub);
	}

	@Override
	public List<SubscriptionDTO> getAllActiveSubscriptionList() {
		List<Subscription> categories = subscriptionRepository.findByIsActiveTrue();
		return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Override
	public Optional<SubscriptionDTO> getSubscriptionDTOByUuid(String uuid) {
		return Optional
				.ofNullable(subscriptionRepository.findByUuidAndIsActiveTrue(uuid).map(this::convertToDTO).orElseThrow(
						() -> new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid)));
	}

	@Override
	public Subscription updateSubscription(String uuid, Subscription subscription) {
		Subscription existingSubscription = subscriptionRepository.findByUuidAndIsActiveTrue(uuid)
				.orElseThrow(() -> new UsernameNotFoundException("No Subscription found for UUID: " + uuid));
		if (!existingSubscription.getPlanName().equals(subscription.getPlanName())) {
			if (subscriptionRepository.existsByPlanNameAndIsActiveTrue(subscription.getPlanName())) {
				throw new RuntimeException("Subscription already exists, try another.");
			}
		}
		existingSubscription.setPlanName(subscription.getPlanName());
		existingSubscription.setDescription(subscription.getDescription());
		if (subscription.getCost() == null || subscription.getCost() < 1) {
			throw new RuntimeException("Minimum Value Should be 1");
		}
		existingSubscription.setCost(subscription.getCost());
		existingSubscription.setSubscriptionType(subscription.getSubscriptionType());
		return subscriptionRepository.save(existingSubscription);
	}

	public boolean deleteSubscription(String uuid) {
	    Optional<Subscription> existingSubscription = subscriptionRepository.findByUuidAndIsActiveTrue(uuid);
	    if (existingSubscription.isEmpty()) {
	        throw new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid);
	    }

	    Subscription subscription = existingSubscription.get();
	    subscription.setActive(false);
	    subscriptionRepository.save(subscription);
	    List<PurchaseSubscription> purchasedSubscriptions = purchaseSubscriptionService.getAllPaxHeadIdBySubscriptionId(subscription.getId());
        for (PurchaseSubscription purchasedSubscription : purchasedSubscriptions) {
            String emailTemplate = "PlanDeletionAlert.html";
            System.out.println("purchaseDate"+purchasedSubscription.getPurchaseDate().toLocalDate());
            System.out.println("ExpiryDate"+ purchasedSubscription.getExpiryDate().toLocalDate());
            LocalDate date=purchasedSubscription.getPurchaseDate().toLocalDate();
            try {
                emailservice.sentPlanDeletionAlertEmail(
                        purchasedSubscription.getPaxUser().getEmail(),  
                        purchasedSubscription.getPaxUser().getName(),   
                        purchasedSubscription.getPlanName(),           
                        date, 
                        purchasedSubscription.getExpiryDate().toLocalDate(), 
                        emailTemplate // Email template
                );
                System.out.println("Email sent to: " + purchasedSubscription.getPaxUser().getEmail());
            } catch (Exception e) {
                System.err.println("Failed to send email to: " + purchasedSubscription.getPaxUser().getEmail());
                e.printStackTrace();
            }
        }
	    return true;
	}

	@Override
	public Subscription getSubscriptionByUuid(String uuid) {
		return subscriptionRepository.findByUuidAndIsActiveTrue(uuid)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
	}

	@Override
	public List<SubscriptionDTO> searchSubscription(String keyword, String sortBy, String direction) {
		Sort sort = Sort.by(Sort.Order.by(sortBy));
		if ("desc".equalsIgnoreCase(direction)) {
			sort = sort.descending();
		} else {
			sort = sort.ascending();
		}
		List<Subscription> subscriptions = subscriptionRepository.searchSubscription(keyword, sort);
		return subscriptions.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Override
	public Page<SubscriptionDTO> getAllPaginatedAndSortedSubscription(int page, int size, String sortBy,
			String direction) {
		Sort.Order order = direction.equalsIgnoreCase("desc") ? Sort.Order.desc(sortBy) : Sort.Order.asc(sortBy);
		Pageable pageable = PageRequest.of(page, size, Sort.by(order));
		return subscriptionRepository.findByIsActiveTrue(pageable).map(this::convertToDTO);
	}

	private SubscriptionDTO convertToDTO(Subscription subscription) {
		SubscriptionDTO dto = new SubscriptionDTO();
		dto.setUuid(subscription.getUuid());
		dto.setPlanName(subscription.getPlanName());
		dto.setDescription(subscription.getDescription());
		dto.setCost(subscription.getCost());
		dto.setSubscriptionType(subscription.getSubscriptionType().name());
		return dto;
	}
}
