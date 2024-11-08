package com.project.course.subscription.service.Impl;

import com.project.course.subscription.dto.SubscriptionDTO;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.repository.SubscriptionRepository;
import com.project.course.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public Subscription createSubscription(Subscription subscription) {
            Subscription sub = new Subscription();
            if (subscriptionRepository.existsByPlanNameAndIsActiveTrue(sub.getPlanName())){
                throw new RuntimeException("PlanName id already exists, try another.");
            }
            sub.setPlanName(subscription.getPlanName());
            sub.setDescription(subscription.getDescription());
            if (sub.getCost() < 1){
            throw new RuntimeException("Minimum Value Should be 1,");
            }
            sub.setCost(subscription.getCost());
            sub.setSubscriptionType(subscription.getSubscriptionType());
            return subscriptionRepository.save(sub);
    }

    @Override
    public Page<SubscriptionDTO> getAllActiveSubscriptions(Pageable pageable) {
        return subscriptionRepository.findByIsActiveTrue(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<SubscriptionDTO> getAllActiveSubscriptionList() {
        List<Subscription> categories = subscriptionRepository.findByIsActiveTrue();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SubscriptionDTO> getSubscriptionDTOByUuid(String uuid) {
        return Optional.ofNullable(subscriptionRepository.findByUuidAndIsActiveTrue(uuid)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid)));
    }

    @Override
    public Optional<Subscription> updateSubscription(String uuid, Subscription subscription) {
        return Optional.ofNullable(subscriptionRepository.findByUuidAndIsActiveTrue(uuid).map(existingSubscription -> {
                    // Update the existing category with values from categoryDetails
                    existingSubscription.setPlanName(subscription.getPlanName());
                    existingSubscription.setDescription(subscription.getDescription());
                    existingSubscription.setCost(subscription.getCost());
                    existingSubscription.setSubscriptionType(subscription.getSubscriptionType());
                    // Save and return the updated category
                    return subscriptionRepository.save(existingSubscription);
                })
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid)));
    }

    @Override
    public boolean deleteSubscription(String uuid) {
        Optional<Subscription> existingSubscription = subscriptionRepository.findByUuidAndIsActiveTrue(uuid);
        if (existingSubscription.isPresent()) {
            Subscription subscription = existingSubscription.get();
            subscription.setActive(false);
            subscriptionRepository.save(subscription);
            return true;
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid);
        }
    }
    
    @Override
    public Subscription getSubscriptionByUuid(String uuid) {
        return subscriptionRepository.findByUuidAndIsActiveTrue(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
    }

    @Override
    public List<SubscriptionDTO> searchSubscription(String keyword) {
        List<Subscription> subscriptions = subscriptionRepository.searchSubscription(keyword);
        return subscriptions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
