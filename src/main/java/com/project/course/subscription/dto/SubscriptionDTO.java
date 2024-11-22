package com.project.course.subscription.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
public class SubscriptionDTO {

    private String uuid;

    private String planName;

    private String description;

    @JsonSerialize(using = INRFormatterSerializer.class)
    private Double cost;

    private String subscriptionType;
}
