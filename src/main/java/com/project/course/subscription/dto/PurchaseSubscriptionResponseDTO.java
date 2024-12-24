package com.project.course.subscription.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PurchaseSubscriptionResponseDTO {

    private String uuid;

    private String paxUserUuid;

    private String subscriptionName;

    private String subscriptionType;

    private Boolean recurring;

    private Boolean paid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime purchaseDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime expiryDate;

}
