package com.motionbridge.motionbridge.product.web.mapper;

import com.motionbridge.motionbridge.product.entity.Product;
import com.motionbridge.motionbridge.subscription.entity.ProductName;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class RestProduct {
    Long id;
    ProductName name;
    BigDecimal price;
    String currency;
    Integer animationQuantity;
    String timePeriod;
    Boolean isActive;
    String background;
    List<RestPresentation> presentations;
    List<RestParameter> parameters;

    public static RestProduct toRestProduct(Product product, List<RestPresentation> presentations, List<RestParameter> parameters) {
        return new RestProduct(
                product.getId(),
                product.getType(),
                product.getPrice(),
                product.getCurrency().toString(),
                product.getAnimationQuantity(),
                product.getTimePeriod().toString(),
                product.getIsActive(),
                product.getBackground(),
                presentations,
                parameters
        );
    }
}
