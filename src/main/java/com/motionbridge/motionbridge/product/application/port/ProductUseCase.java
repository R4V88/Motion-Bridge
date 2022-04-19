package com.motionbridge.motionbridge.product.application.port;

import com.motionbridge.motionbridge.product.entity.Product;

import java.util.List;

public interface ProductUseCase {
    List<Product> getActiveProducts();
}
