package com.motionbridge.motionbridge.product.application;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class RestActiveProduct {
    String name;
    BigDecimal price;
    String currency;
    Integer animationQuantity;
    String timePeriod;
}
