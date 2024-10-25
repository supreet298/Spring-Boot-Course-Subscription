package com.project.course.subscription.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "PhoneNumber is mandatory and cannot be null.")
    private String phoneNumber;

    @NotNull(message = "HeadID is mandatory and cannot be null.")
    private Long headId;

    @NotNull(message = "Relation is mandatory and cannot be blank.")
    private String relation;
}
