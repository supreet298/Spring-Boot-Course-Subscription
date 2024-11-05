package com.project.course.subscription.controller;

import com.project.course.subscription.dto.CustomPageResponse;
import com.project.course.subscription.dto.PurchaseHistoryDTO;
import com.project.course.subscription.service.PurchaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<CustomPageResponse<PurchaseHistoryDTO>> getPurchaseHistories(
            @PathVariable String uuid,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchaseHistoryDTO> purchaseHistoriesPages = purchaseHistoryService.getPurchaseHistoriesByPaxUserUuid(uuid, pageable);
        // Map the Page to CustomPageResponse
        CustomPageResponse<PurchaseHistoryDTO> response = new CustomPageResponse<>(
                purchaseHistoriesPages.getContent(),
                purchaseHistoriesPages.getNumber(),
                purchaseHistoriesPages.getSize(),
                purchaseHistoriesPages.getTotalElements(),
                purchaseHistoriesPages.getTotalPages(),
                purchaseHistoriesPages.isFirst(),
                purchaseHistoriesPages.isLast()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active/{uuid}")
    public ResponseEntity<List<PurchaseHistoryDTO>> getActivePurchaseHistories(@PathVariable String uuid) {
        List<PurchaseHistoryDTO> activePurchases = purchaseHistoryService.getActivePurchaseHistories(uuid);
        return ResponseEntity.ok(activePurchases);
    }

}
