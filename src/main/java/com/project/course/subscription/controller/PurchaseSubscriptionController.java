package com.project.course.subscription.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.dto.PurchaseSubscriptionResponseDTO;
import com.project.course.subscription.service.PurchaseSubscriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/admin/purchaseSubscription")
public class PurchaseSubscriptionController {

    @Autowired
    private PurchaseSubscriptionService purchaseSubscriptionService;

    @PostMapping
    public ResponseEntity<PurchaseSubscriptionDTO> purchaseSubscription(@Valid @RequestBody PurchaseSubscriptionDTO purchaseSubscriptionDTO) {
        PurchaseSubscriptionDTO purchaseSubscription = purchaseSubscriptionService.createPurchaseSubscription(purchaseSubscriptionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseSubscription);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseSubscriptionDTO>> getAllPurchaseSubscriptions() {
        List<PurchaseSubscriptionDTO> purchaseSubscription = purchaseSubscriptionService.getAllPurchaseSubscriptions();
        return ResponseEntity.ok(purchaseSubscription);
    }

    @GetMapping("/{uuid}")
    public Optional<PurchaseSubscriptionDTO> getPurchaseSubscriptionByUuid(@PathVariable String uuid) {
        return purchaseSubscriptionService.getPurchaseSubscriptionByUuid(uuid);
    }
    

    @DeleteMapping("/{uuid}/disable")
    public ResponseEntity<String> disableRecurring(@PathVariable String uuid) {
        boolean success = purchaseSubscriptionService.disableRecurringForSubscription(uuid);
        if (success) {
            return ResponseEntity.ok("Recurring has been disabled for the subscription.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subscription not found.");
        }
    }

    @PutMapping("/{uuid}/pay")
    public ResponseEntity<String> paySubscription(@PathVariable String uuid) {
        boolean success = purchaseSubscriptionService.paySubscription(uuid);
        if (success) {
            return ResponseEntity.ok("Paid the subscription.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subscription not found.");
        }
    }

    @GetMapping("/active/{uuid}")
    public List<PurchaseSubscriptionResponseDTO> getActiveSubscriptionsByPaxUserUuid(@PathVariable String uuid) {
        return purchaseSubscriptionService.getActiveSubscriptionsByPaxUserUuid(uuid);
    }
}
