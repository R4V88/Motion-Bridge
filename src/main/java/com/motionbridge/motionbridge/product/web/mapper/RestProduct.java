package com.motionbridge.motionbridge.product.web.mapper;

import com.motionbridge.motionbridge.product.entity.Product;
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

    public static RestProduct toRestProduct(Product product) {
        return new RestProduct(
                product.getId(),
                product.getTitle(),
                product.getPrice(),
                product.getCurrency().toString(),
                product.getAnimationQuantity(),
                product.getTimePeriod().toString(),
                product.getIsActive()
        );
    }
}
