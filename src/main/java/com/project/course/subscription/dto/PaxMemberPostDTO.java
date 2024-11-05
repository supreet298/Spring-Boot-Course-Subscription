package com.project.course.subscription.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PaxMemberPostDTO {

    @NotBlank(message = "Username is mandatory and cannot be blank.")
    private String userName;
    
    @NotBlank(message = "Head's Uuid is mandatory and cannot be blank.")
    private String uuid;
    
    @NotBlank(message = "Email is mandatory and cannot be blank.")
    @Email(message = "Email must be in a valid format.")
    private String email;

    @NotNull(message = "HeadID is mandatory and cannot be null.")
    private Long headId;
    
    @Column(columnDefinition = "Text")
    @NotBlank(message = "Address is mandatory and cannot be blank.")
    private String address;
    
    @NotNull(message = "Country is mandatory and cannot be null.")
    private String country;
    
    @NotBlank(message = "Country code is required")
    @Pattern(regexp = "\\+\\d{1,3}", message = "Invalid country code format (e.g., +1, +91)")
    private String countryCode;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotNull(message = "Relation is mandatory and cannot be blank.")
    private String relation;
    
    
}