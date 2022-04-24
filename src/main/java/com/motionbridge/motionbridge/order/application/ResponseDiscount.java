package com.motionbridge.motionbridge.order.application;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ResponseDiscount {
    String subscriptionPeriod;
    String subscriptionType;
    String durationPeriod;
    Integer duration;
    Integer value;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Boolean isActive;
}
