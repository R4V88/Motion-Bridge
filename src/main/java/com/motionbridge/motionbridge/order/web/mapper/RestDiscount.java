package com.motionbridge.motionbridge.order.web;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class RestDiscount {
    String subscriptionPeriod;
    String subscriptionType;
    String durationPeriod;
    Integer duration;
    Integer value;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Boolean isActive;
}
