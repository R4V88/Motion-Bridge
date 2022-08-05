package com.motionbridge.motionbridge.product.web.mapper;

import com.motionbridge.motionbridge.product.entity.Product;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class RestActiveProduct {
    String name;
    BigDecimal price;
    String currency;
    Integer animationQuantity;
    String timePeriod;

    public static RestActiveProduct toRestActiveProduct(Product product) {
        return new RestActiveProduct(
                product.getName().toString(),
                product.getPrice(),
                product.getCurrency().toString().toLowerCase(),
                product.getAnimationQuantity(),
                product.getTimePeriod().toString()
        );

    }
}
