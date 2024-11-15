package com.project.course.subscription.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "purchase_history")
public class PurchaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pax_user_id", referencedColumnName = "id", nullable = false, updatable = false)
    private PaxUser paxUser;

    @ManyToOne
    @JoinColumn(name = "subscription_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Subscription subscription;

    @Column(name = "client_name", nullable = false, updatable = false)
    private String clientName;
    
    @Column(name = "client_email", nullable = false, updatable = false)
    private String clientEmail;
    
    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(name = "subscription_type", nullable = false)
    private String SubscriptionType;
    
    @Column(name = "renewal_count", nullable = false)
    private int renewalCount;

    @Column(name = "purchase_subscription_uuid", nullable = false)
    private String purchaseSubscriptionUuid;

    @Column(name = "purchase_date", nullable = false,updatable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
    
    @Column(name = "notification_type", nullable = false)
    private  String notificationType;

    @Column(name = "cost", nullable = false)
    private Double cost;

    @Column(name = "paid", nullable = false)
    private Boolean paid;
}
