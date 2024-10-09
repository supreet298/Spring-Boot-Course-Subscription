package com.project.course.subscription.service.Impl;

import com.project.course.subscription.dto.SubscriptionDTO;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.repository.SubscriptionRepository;
import com.project.course.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return subscriptionRepository.save(subscription);
    }

    @Override
    public List<SubscriptionDTO> getAllActiveSubscriptions() {
        List<Subscription> categories = subscriptionRepository.findByIsActiveTrue();
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SubscriptionDTO> getSubscriptionByUuid(String uuid) {
        return Optional.ofNullable(subscriptionRepository.findByUuid(uuid)
                .filter(Subscription::isActive)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid)));
    }

    @Override
    public Optional<Subscription> updateSubscription(String uuid, Subscription subscription) {
        return Optional.ofNullable(subscriptionRepository.findByUuid(uuid).map(existingSubscription -> {
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
        Optional<Subscription> existingSubscription = subscriptionRepository.findByUuid(uuid);
        if (existingSubscription.isPresent()) {
            Subscription subscription = existingSubscription.get();
            subscription.setActive(false);
            subscriptionRepository.save(subscription);
            return true;
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid);
        }
    }

    public Subscription getSubscriptionById(Long id){
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SubscriptionType not found"));
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
