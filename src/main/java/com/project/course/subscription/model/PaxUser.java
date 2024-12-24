package com.project.course.subscription.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Table(name = "pax_user")
@EntityListeners(AuditingEntityListener.class)
public class PaxUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @NotBlank(message = "Username is mandatory and cannot be blank.")
    @Column(name = "name")
    private String name;
    
    @NotBlank(message = "Email is mandatory and cannot be blank.")
    @Email(message = "Email must be in a valid format.")
    private String email;

    @NotBlank(message = "Country code is required")
    @Pattern(regexp = "\\+\\d{1,4}", message = "Invalid country code format (e.g., +1, +91)")
    private String countryCode;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;
    
    @Column(columnDefinition = "Text")
    @NotBlank(message = "Address is mandatory and cannot be blank.")
    private String address;
    
    @NotNull(message = "Country is mandatory and cannot be null.")
    private String country;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Relation relation;

    @Column(name = "head_uuid")
    private String headUuid;

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

    public enum Type{

        HEAD,
        MEMBER
    }

    public enum Relation{

        FATHER,
        WIFE,
        SON,
        DAUGHTER
    }
}