package com.project.course.subscription.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PurchaseHistoryDTO {

    private String paxUserUuid;

    private String purchaseSubscriptionUuid;

    private String clientName;

    private String clientEmail;

    private String planName;

    private String subscriptionType;

    @JsonSerialize(using = INRFormatterSerializer.class)
    private Double cost;

    private int renewalCount;

    private LocalDateTime purchaseDate;

    private LocalDateTime expiryDate;

    private String notificationType;
}
