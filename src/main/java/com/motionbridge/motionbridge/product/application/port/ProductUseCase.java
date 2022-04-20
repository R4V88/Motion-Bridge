package com.motionbridge.motionbridge.product.application.port;

import com.motionbridge.motionbridge.product.application.ActiveProduct;

import java.util.List;

public interface ProductUseCase {
    List<ActiveProduct> getActiveProducts();
}
