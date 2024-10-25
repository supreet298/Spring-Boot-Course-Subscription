package com.project.course.subscription.controller;

import com.project.course.subscription.dto.PurchaseHistoryDTO;
import com.project.course.subscription.service.PurchaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("api/admin/purchaseHistory")
public class PurchaseHistoryController {

    @Autowired
    private PurchaseHistoryService purchaseHistoryService;

    @GetMapping
    public ResponseEntity<List<PurchaseHistoryDTO>> getAllPurchaseSubscriptionHistory() {
        List <PurchaseHistoryDTO> history = purchaseHistoryService.getAllPurchaseSubscriptionHistory();
        return ResponseEntity.ok(history);
    }

}
