package com.project.course.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseSubscriptionDTO {

    private String uuid;

    private String planName;

    private String paxUserHead;

    private boolean recurring;

    private boolean paid;

    private LocalDateTime purchaseDate;

    private LocalDateTime expiryDate;

}
