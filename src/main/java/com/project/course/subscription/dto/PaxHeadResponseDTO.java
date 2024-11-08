package com.project.course.subscription.dto;

import lombok.Data;

@Data
public class PaxHeadResponseDTO {

    private String uuid;

    private String name;

    private String email;

    private String phoneNumber;

    private String address;

    private String country;

    private String type;
}
