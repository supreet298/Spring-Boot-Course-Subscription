package com.project.course.subscription.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    private Boolean paid;

    private int renewalCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime purchaseDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime expiryDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime paidDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime cancelRecurringDate;

    private Boolean recurring;

    private String notificationType;
}
