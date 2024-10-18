package com.project.course.subscription.controller;

import com.project.course.subscription.dto.SubscriptionDTO;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("api/admin/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Subscription> createSubscription(@Valid @RequestBody Subscription subscription) {
        Subscription createdSubscription = subscriptionService.createSubscription(subscription);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubscription);
    }

    @GetMapping
    public String getAllActiveSubscriptions(Model model) {
        List<SubscriptionDTO> subscriptions = subscriptionService.getAllActiveSubscriptions();
        model.addAttribute("subscription",subscriptions);
        return "subscriptions/index";
    }

    @GetMapping("/{uuid}")
    public Optional<SubscriptionDTO> getSubscriptionByUuid(@PathVariable String uuid) {
        return subscriptionService.getSubscriptionByUuid(uuid);
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
