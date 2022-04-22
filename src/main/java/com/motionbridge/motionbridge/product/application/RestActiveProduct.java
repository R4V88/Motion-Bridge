package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.entity.Product;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class RestActiveProduct {
    Product.ProductName name;
    BigDecimal price;
    String currency;
    Integer animationQuantity;
    Integer timePeriod;
}
