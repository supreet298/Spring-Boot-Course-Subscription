package com.project.course.subscription.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PurchaseHistoryRequestDTO {

    private Long paxUserId;
    private Long subscriptionId;
    private int renewalCount;
    private LocalDateTime expiryDate;
}
