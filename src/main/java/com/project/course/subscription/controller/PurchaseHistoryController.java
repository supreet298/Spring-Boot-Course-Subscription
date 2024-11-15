package com.project.course.subscription.controller;

import com.project.course.subscription.dto.PurchaseHistoryDTO;
import com.project.course.subscription.service.PurchaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public ResponseEntity<Page<PurchaseHistoryDTO>> getPurchaseHistories(@PathVariable String uuid,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "planName") String sortBy, @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {

        // Define the DateTimeFormatter for the "dd-MM-yyyy" format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDateTime start = null;
        LocalDateTime end = null;

        // Parse the start and end dates only if they are provided
        if (startDate != null && !startDate.isEmpty()) {
            start = LocalDate.parse(startDate, formatter).atStartOfDay();
        }

        if (endDate != null && !endDate.isEmpty()) {
            end = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);
        }

        // Call the service to get the filtered results
        Page<PurchaseHistoryDTO> purchaseHistoriesPages = purchaseHistoryService.getPurchaseHistoriesByPaxUserUuid(
                uuid, page, size, sortBy, direction, start, end);

        return ResponseEntity.ok(purchaseHistoriesPages);
    }

}
