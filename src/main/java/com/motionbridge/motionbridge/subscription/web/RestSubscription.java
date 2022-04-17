package com.motionbridge.motionbridge.subscription.web;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class RestSubscription {
    Boolean isActive;
    LocalDateTime expirationDate;
    String type;
    Integer animationsLeft;
}
