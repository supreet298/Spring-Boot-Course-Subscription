package com.project.course.subscription.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.course.subscription.model.PurchaseSubscription;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseSubscriptionDTO {

    @JsonIgnore
    private String uuid;

    private String paxUserUuid;

    private String subscriptionUuid;

    private boolean recurring;

    private PurchaseSubscription.NotificationType notificationType;

    private Boolean paid;
        
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime purchaseDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime expiryDate;
    
    private String checkOutUrl;
		
    private String planName;
    
    private Double cost;

    private String transactionId;
    
    private String paymentStatus;
        
    @Enumerated(EnumType.STRING)
    private Currency currency;
    
    public enum Currency
    {
    	USD,INR,
    }
    
    @Enumerated(EnumType.STRING)
    private SessionMode sessionMode;
    
    public enum SessionMode {
        PAYMENT, SETUP, SUBSCRIPTION
    }
    
    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethodType;
    
    public enum PaymentMethodType {
        AMAZON_PAY, CARD
    }
    
    private String subscriptionType;

}
