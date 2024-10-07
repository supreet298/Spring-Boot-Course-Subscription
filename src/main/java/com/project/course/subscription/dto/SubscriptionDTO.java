package com.project.course.subscription.dto;

import lombok.Data;

@Data
public class SubscriptionDTO {

    private String uuid;

    private String planName;

    private String description;

    private Double cost;

    private String subscriptionType;
}
