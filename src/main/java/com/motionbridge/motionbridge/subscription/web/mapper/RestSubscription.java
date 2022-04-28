package com.motionbridge.motionbridge.subscription.web;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class RestSubscription {
    Boolean isActive;
    BigDecimal currentPrice;
    LocalDateTime endDate;
    String type;
    Integer animationsLimit;
    String timePeriod;
}
