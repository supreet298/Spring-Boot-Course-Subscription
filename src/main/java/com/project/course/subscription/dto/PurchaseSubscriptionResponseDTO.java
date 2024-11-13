package com.project.course.subscription.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PurchaseSubscriptionResponseDTO {

    private String uuid;

    private String paxUserUuid;

    private String subscriptionName;

    private String subscriptionType;

    private Boolean recurring;

    private LocalDateTime purchaseDate;

    private LocalDateTime expiryDate;

}
