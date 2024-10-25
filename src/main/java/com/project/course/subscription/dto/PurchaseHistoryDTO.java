package com.project.course.subscription.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PurchaseHistoryDTO {

    private String clientName;

    private String clientEmail;

    private String planName;

    private int renewalCount;

    private LocalDateTime purchaseDate;

    private LocalDateTime expiryDate;

    private String notificationType;
}
