package com.project.course.subscription.controller;

import com.project.course.subscription.dto.CustomPageResponse;
import com.project.course.subscription.dto.SubscriptionDTO;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/admin/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Object> createSubscription(@Valid @RequestBody Subscription subscription) {
        try {
            Subscription createdSubscription = subscriptionService.createSubscription(subscription);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubscription);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<SubscriptionDTO>> getAllActiveSubscriptions() {
        List<SubscriptionDTO> subscriptions = subscriptionService.getAllActiveSubscriptionList();
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping
    public ResponseEntity<CustomPageResponse<SubscriptionDTO>> getAllActiveSubscriptions(
            @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SubscriptionDTO> subscriptionsPage = subscriptionService.getAllActiveSubscriptions(pageable);
        // Map the Page to CustomPageResponse
        CustomPageResponse<SubscriptionDTO> response = new CustomPageResponse<>(
                subscriptionsPage.getContent(),
                subscriptionsPage.getNumber(),
                subscriptionsPage.getSize(),
                subscriptionsPage.getTotalElements(),
                subscriptionsPage.getTotalPages(),
                subscriptionsPage.isFirst(),
                subscriptionsPage.isLast()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uuid}")
    public Optional<SubscriptionDTO> getSubscriptionByUuid(@PathVariable String uuid) {
        return subscriptionService.getSubscriptionDTOByUuid(uuid);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SubscriptionDTO>> searchSubscription(@RequestParam String keyword){
        List<SubscriptionDTO> subscriptions = subscriptionService.searchSubscription(keyword);
        return ResponseEntity.ok(subscriptions);
    }

    @PutMapping("/{uuid}")
    public Optional<Subscription> updateCategory(@PathVariable String uuid, @RequestBody Subscription Subscription) {
        return subscriptionService.updateSubscription(uuid, Subscription);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> deleteUserByUuid(@PathVariable String uuid) {
        boolean deactivated = subscriptionService.deleteSubscription(uuid);
        if (deactivated) {
            return ResponseEntity.ok("Subscription is deleted");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
