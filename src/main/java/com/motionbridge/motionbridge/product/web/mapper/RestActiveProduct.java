package com.motionbridge.motionbridge.product.web.mapper;

import com.motionbridge.motionbridge.product.entity.Product;
import com.motionbridge.motionbridge.subscription.entity.ProductName;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class RestActiveProduct {
    Long id;
    String title;
    ProductName type;
    BigDecimal price;
    String currency;
    Integer animationQuantity;
    String timePeriod;
    String background;
    List<RestPresentation> presentations;
    List<RestParameter> parameters;

    public static RestActiveProduct toRestActiveProduct(Product product, List<RestPresentation> presentations, List<RestParameter> parameters) {
        return new RestActiveProduct(
                product.getId(),
                product.getType().toString(),
                product.getType(),
                product.getPrice(),
                product.getCurrency().toString().toLowerCase(),
                product.getAnimationQuantity(),
                product.getTimePeriod().toString(),
                product.getBackground(),
                presentations,
                parameters
        );

    }

}
