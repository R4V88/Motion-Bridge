package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.entity.Product.ProductName;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class ActiveProduct {
    ProductName name;
    BigDecimal price;
}
