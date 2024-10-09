package com.project.course.subscription.controller;

import com.project.course.subscription.model.PurchaseHistory;
import com.project.course.subscription.service.PurchaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<PurchaseHistory> getAllPurchaseSubscriptionHistory() {
        return purchaseHistoryService.getAllPurchaseSubscriptionHistory();
    }

}
