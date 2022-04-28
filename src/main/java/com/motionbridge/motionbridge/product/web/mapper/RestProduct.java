package com.motionbridge.motionbridge.product.web;

import com.motionbridge.motionbridge.subscription.entity.ProductName;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class RestProduct {
    Long id;
    ProductName name;
    BigDecimal price;
    String currency;
    Integer animationQuantity;
    String timePeriod;
    Boolean isActive;
}
