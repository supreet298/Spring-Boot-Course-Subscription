package com.project.course.subscription.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "purchase_subscription")
@EntityListeners(AuditingEntityListener.class)
public class PurchaseSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "pax_user_id",referencedColumnName = "id",updatable = false)
    private PaxUser paxUser;

    @ManyToOne
    @JoinColumn(name = "subscription_id", referencedColumnName = "id",updatable = false)
    private Subscription subscription;

    private boolean recurring;

    @Column(nullable = false)
    private Boolean paid;

    private LocalDateTime purchaseDate;

    private LocalDateTime expiryDate;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    private LocalDateTime lastModifiedDate;

    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Version
    private int version;

    private String planName;

    private Double cost;

    @Column(name = "subscription_type")
    private String subscriptionType;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    
    public enum NotificationType
    {
    	EMAIL,WHATSAPP,BOTH
    }

}
