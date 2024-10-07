package com.project.course.subscription.controller;

import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.model.PurchaseSubscription;
import com.project.course.subscription.service.PurchaseSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/admin/purchaseSubscription")
public class PurchaseSubscriptionController {

    @Autowired
    private PurchaseSubscriptionService purchaseSubscriptionService;

    @PostMapping
    public ResponseEntity<PurchaseSubscription> createSubscription(@RequestBody PurchaseSubscription purchaseSubscription) {
        PurchaseSubscription CreatedpurchaseSubscription = purchaseSubscriptionService.createPurchaseSubscription(purchaseSubscription);
        return ResponseEntity.status(HttpStatus.CREATED).body(CreatedpurchaseSubscription);
    }

    @GetMapping
    public ResponseEntity<List<PurchaseSubscriptionDTO>> getAllPurchaseSubscriptions() {
        List<PurchaseSubscriptionDTO> purchaseSubscription = purchaseSubscriptionService.getAllPurchaseSubscriptions();
        return ResponseEntity.ok(purchaseSubscription);
    }
}
