package com.project.course.subscription.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admin_user")
public class AdminUser {

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

    @NotBlank(message = "Password is mandatory and cannot be blank.")
    private String password;

    @NotNull(message = "PhoneNumber is mandatory and cannot be blank.")
    @Column(name = "phone_number", unique = true)
    private Long phoneNumber;

    @NotBlank(message = "Address is mandatory and cannot be blank.")
    private String address;
}
