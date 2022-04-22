package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.entity.Product.ProductName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class RestProduct {
    Long id;
    ProductName name;
    BigDecimal price;
    String currency;
    Integer animationQuantity;
    Integer timePeriod;
    Boolean isActive;
}
