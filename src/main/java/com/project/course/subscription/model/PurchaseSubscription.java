package com.project.course.subscription.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "purchase_subscription")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PurchaseSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "pax_user_id",referencedColumnName = "id",updatable = false)

    @NotNull(message = "PaxUser is mandatory and cannot be blank.")
    private PaxUser paxUser;

    @ManyToOne
    @JoinColumn(name = "subscription_id", referencedColumnName = "id",updatable = false)
  //  @NotBlank(message = "Subscription is mandatory and cannot be blank.")
    @NotNull(message = "Subscription is mandatory and cannot be blank.")
    private Subscription subscription;

    private boolean recurring;

    @Column(nullable = false)
    @NotNull(message = "Paid cannot be null")
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
    
    
    public enum NotificationType
    {
    	EMAIL,WHATSAPP,BOTH
    }
    
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
}
