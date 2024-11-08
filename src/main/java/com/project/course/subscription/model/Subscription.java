package com.project.course.subscription.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
@Table(name = "subscription")
@EntityListeners(AuditingEntityListener.class)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @NotBlank(message = "PlanName is mandatory and cannot be blank.")
    @Column(name = "plan_Name")
    private String planName;

    @Column(columnDefinition = "Text")
    @NotBlank(message = "Description is mandatory and cannot be blank.")
    private String description;

    @NotNull(message = "Cost is mandatory and cannot be blank.")
    @Min(value = 1 ,message = "minimum value should be 1.")
    private Double cost;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "SubscriptionType is mandatory and cannot be blank.")
    private SubscriptionType subscriptionType;

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

    public enum SubscriptionType {
        MONTHLY,
        QUARTERLY,
        HALF_YEARLY,
        YEARLY
    }
}
