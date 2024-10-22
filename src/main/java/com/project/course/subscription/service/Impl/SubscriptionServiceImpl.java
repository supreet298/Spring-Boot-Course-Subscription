package com.project.course.subscription.service.Impl;

import com.project.course.subscription.dto.SubscriptionDTO;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.repository.SubscriptionRepository;
import com.project.course.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public void createSubscription(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    @Override
    public List<Subscription> getAllActiveSubscriptions() {
        return subscriptionRepository.findByIsActiveTrue();
//        List<Subscription> categories = subscriptionRepository.findByIsActiveTrue();
//        return categories.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
    }

    
    @Override
    public Optional<SubscriptionDTO> getSubscriptionByUuid(String uuid) {
        return Optional.ofNullable((subscriptionRepository.findByUuid(uuid)
                .filter(Subscription::isActive)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Subscription not found with UUID: " + uuid))));
    }

    @Override
    public void updateSubscription(String uuid, Subscription subscription) {
        Subscription  existingSubscription= new Subscription();
        existingSubscription.setPlanName(subscription.getPlanName());
        existingSubscription.setDescription(subscription.getDescription());
        existingSubscription.setCost(subscription.getCost());
        existingSubscription.setSubscriptionType(subscription.getSubscriptionType());
        subscriptionRepository.save(existingSubscription);
    }

    @Override
    public void deleteSubscription(String uuid) {
        Optional<Subscription> existingSubscription = subscriptionRepository.findByUuid(uuid);
        if (existingSubscription.isPresent()) {
            Subscription subscription = existingSubscription.get();
            subscription.setActive(false);
            subscriptionRepository.save(subscription);
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
        dto.setPlanName(subscription.getPlanName());
        dto.setDescription(subscription.getDescription());
        dto.setCost(subscription.getCost());
        dto.setSubscriptionType(subscription.getSubscriptionType().name());
        return dto;
    }
}
