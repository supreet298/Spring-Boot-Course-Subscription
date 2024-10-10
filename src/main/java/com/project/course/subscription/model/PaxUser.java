package com.project.course.subscription.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@Table(name = "pax_user")
@EntityListeners(AuditingEntityListener.class)
public class PaxUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @NotBlank(message = "Username is mandatory and cannot be blank.")
    @Column(name = "user_name")
    private String userName;

    @NotBlank(message = "Email is mandatory and cannot be blank.")
    @Email(message = "Email must be in a valid format.")
    @Column(unique = true)
    private String email;

    @NotNull(message = "PhoneNumber is mandatory and cannot be null.")
    @Column(name = "phone_number", unique = true)
    private Long phoneNumber;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Relation relation;

    @Column(name = "head_id")
    private Long headId;

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
