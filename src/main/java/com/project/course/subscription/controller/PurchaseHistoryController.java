package com.project.course.subscription.controller;

import com.project.course.subscription.dto.PurchaseHistoryDTO;
import com.project.course.subscription.service.PurchaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/admin/purchaseHistory")
public class PurchaseHistoryController {

    @Autowired
    private PurchaseHistoryService purchaseHistoryService;

    @GetMapping
    public ResponseEntity<List<PurchaseHistoryDTO>> getAllPurchaseSubscriptionHistory() {
        List<PurchaseHistoryDTO> history = purchaseHistoryService.getAllPurchaseSubscriptionHistory();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Page<PurchaseHistoryDTO>> getPurchaseHistories(@PathVariable String uuid,@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "planName") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) {
        Page<PurchaseHistoryDTO> purchaseHistoriesPages = purchaseHistoryService.getPurchaseHistoriesByPaxUserUuid(uuid,page, size, sortBy, direction);
        return ResponseEntity.ok(purchaseHistoriesPages);
    }

}
